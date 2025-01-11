package pt.isel.pdm.chimp.infrastructure.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import kotlin.time.Duration.Companion.seconds

class SSEWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val dependencies: DependenciesContainer = context.applicationContext as DependenciesContainer

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            startSSEConnection(this)
        }
        return Result.success()
    }

    private suspend fun startSSEConnection(scope: CoroutineScope) {
        dependencies.chimpService.eventService.initialize(
            scope = scope,
            session = dependencies.sessionManager,
        )
        dependencies.chimpService.eventService.awaitInitialization(30.seconds)
        dependencies.chimpService.eventService.eventFlow.collect { event ->
            Log.d(TAG, "Received event: $event")
            handleDataSync(event)
        }
    }

    private suspend fun handleDataSync(event: Event) {
        when (event) {
            is Event.ChannelEvent.CreatedEvent -> {
                dependencies.storage.channelRepository.updateChannels(listOf(event.channel))
            }
            is Event.ChannelEvent.UpdatedEvent -> {
                if (event.channel.members.none { it.id == dependencies.sessionManager.session.firstOrNull()?.user?.id }) {
                    dependencies.storage.channelRepository.deleteChannel(event.channel.id)
                } else {
                    dependencies.storage.channelRepository.updateChannels(listOf(event.channel))
                }
            }
            is Event.ChannelEvent.DeletedEvent -> {
                dependencies.storage.channelRepository.deleteChannel(event.channelId)
            }
            is Event.MessageEvent.CreatedEvent -> {
                dependencies.storage.messageRepository.updateMessages(listOf(event.message))
            }
            is Event.MessageEvent.UpdatedEvent -> {
                dependencies.storage.messageRepository.updateMessages(listOf(event.message))
            }
            is Event.MessageEvent.DeletedEvent -> {
                dependencies.storage.messageRepository.deleteMessage(event.messageId)
            }
            else -> {}
        }
    }
}
