package pt.isel.pdm.chimp.infrastructure.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer
import kotlin.time.Duration.Companion.seconds

class SSEWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val dependencies: DependenciesContainer = context.applicationContext as DependenciesContainer

    override suspend fun doWork(): Result {
        withContext(Dispatchers.Default) {
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
        }
    }
}
