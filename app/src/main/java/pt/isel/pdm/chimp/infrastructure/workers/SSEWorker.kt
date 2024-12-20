package pt.isel.pdm.chimp.infrastructure.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer

class SSEWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val dependencies: DependenciesContainer = context.applicationContext as DependenciesContainer

    override suspend fun doWork(): Result {
        val job: Job?
        withContext(Dispatchers.IO) {
            job = startSSEConnection(this)
        }
        job?.join()
        Log.d(TAG, "SSEWorker finished")
        return Result.success()
    }

    private fun startSSEConnection(scope: CoroutineScope) =
        scope.launch {
            dependencies.chimpService.eventService.initialize(scope = this, session = dependencies.sessionManager)
            delay(1000)
            dependencies.chimpService.eventService.eventFlow.collect { event ->
                Log.d(TAG, "Received event: $event")
            }
        }
}
