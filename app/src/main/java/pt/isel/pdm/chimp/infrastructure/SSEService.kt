package pt.isel.pdm.chimp.infrastructure

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.R

class SSEService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private lateinit var dependencies: DependenciesContainer

    override fun onCreate() {
        super.onCreate()
        dependencies = application as DependenciesContainer
        startSSEConnection()
    }

    private fun startForegroundService() {
        val channelId = "SSEServiceChannel"
        val channel =
            NotificationChannel(
                channelId,
                "SSE Service Channel",
                NotificationManager.IMPORTANCE_LOW,
            )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("SSE Service")
                .setContentText("Listening for events")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

        startForeground(1, notification)
    }

    private fun startSSEConnection() {
        serviceScope.launch {
            dependencies.chimpService.eventService.initialize(scope = this, session = dependencies.sessionManager)
            delay(1000)
            dependencies.chimpService.eventService.eventFlow.collect { event ->
                Log.d(TAG, "Received event: $event")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        dependencies.chimpService.eventService.destroy()
    }
}
