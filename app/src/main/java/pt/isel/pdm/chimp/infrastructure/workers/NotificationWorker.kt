package pt.isel.pdm.chimp.infrastructure.workers

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.screens.channel.channelInvitations.ChannelInvitationsActivity
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import kotlin.time.Duration.Companion.seconds

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val dependencies: DependenciesContainer =
        context.applicationContext as DependenciesContainer
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationChannel =
        NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )

    init {
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override suspend fun doWork(): Result {
        val job: Job?
        withContext(Dispatchers.IO) {
            listenForMessageNotifications(this)
            job = listenForInvitationNotifications(this)
        }
        job?.join()
        return Result.success()
    }

    private fun listenForMessageNotifications(scope: CoroutineScope) =
        scope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.messageEventFlow.collect { event ->
                if (event is Event.MessageEvent.CreatedEvent) {
                    notifyMessage(event)
                }
            }
        }

    private fun listenForInvitationNotifications(scope: CoroutineScope) =
        scope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.invitationEventFlow.collect { event ->
                if (event is Event.InvitationEvent.CreatedEvent) {
                    notifyInvitation(event)
                }
            }
        }

    private suspend fun notifyMessage(event: Event.MessageEvent.CreatedEvent) {
        val channel = getChannel(event.message.channelId) ?: return
        val intent =
            Intent(applicationContext, ChannelsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationId = event.message.channelId.value.hashCode()
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val existingNotification =
            notificationManager.activeNotifications.find { it.id == notificationId }

        val inboxStyle = NotificationCompat.InboxStyle()

        if (existingNotification != null) {
            val existingLines =
                existingNotification.notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)
            existingLines?.forEach { inboxStyle.addLine(it) }
        }

        inboxStyle.addLine(
            applicationContext.getString(
                R.string.new_message_notification_content,
                event.message.author.name.value,
                event.message.content,
            ),
        )

        val notification =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(
                    applicationContext.getString(
                        R.string.new_message_notification_title,
                        channel.name.value,
                    ),
                )
                .setContentInfo(channel.name.value)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(inboxStyle)
                .build()

        sendNotification(notification, notificationId)
    }

    private suspend fun notifyInvitation(event: Event.InvitationEvent.CreatedEvent) {
        val user = dependencies.sessionManager.session.firstOrNull()?.user
        val intent =
            Intent(applicationContext, ChannelInvitationsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (user?.id == event.invitation.invitee.id) {
            val notification =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(
                        applicationContext.getString(
                            R.string.new_invitation_notification_title,
                            event.invitation.channel.name.value,
                        ),
                    )
                    .setContentText(
                        applicationContext.getString(
                            R.string.new_invitation_notification_content,
                            event.invitation.inviter.name.value,
                            event.invitation.role.name,
                        ),
                    )
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentIntent(pendingIntent)
                    .build()
            sendNotification(notification, event.eventId.toInt())
        }
    }

    private fun sendNotification(
        notification: Notification,
        eventId: Int,
    ) {
        with(NotificationManagerCompat.from(applicationContext)) {
            if (!ChimpApplication.isInForeground && ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(eventId, notification)
            }
        }
    }

    private suspend fun getChannel(channelId: Identifier): Channel? {
        val session = dependencies.sessionManager.session.firstOrNull() ?: return null
        return (
            dependencies.chimpService.channelService.getChannel(
                channelId,
                session,
            ) as? Success
        )?.value
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "${ChimpApplication.TAG} Notification Channel"
    }
}
