package pt.isel.pdm.chimp

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManager
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManagerImpl
import pt.isel.pdm.chimp.infrastructure.services.http.ChimpServiceHttp
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.infrastructure.session.SessionManagerPreferencesDataStore
import pt.isel.pdm.chimp.infrastructure.storage.Storage
import pt.isel.pdm.chimp.infrastructure.storage.firestore.FireStoreStorage
import pt.isel.pdm.chimp.infrastructure.workers.NotificationWorker
import pt.isel.pdm.chimp.infrastructure.workers.SSEWorker

/**
 * Represents the dependencies container for the Chimp application.
 */
interface DependenciesContainer {
    val chimpService: ChimpService
    val sessionManager: SessionManager
    val entityReferenceManager: EntityReferenceManager
    val storage: Storage
}

class ChimpApplication : Application(), DependenciesContainer {
    init {
        instance = this
    }

    private val preferencesDataStore: DataStore<Preferences> by preferencesDataStore("chimp_preferences")

    private val client by lazy { createHttpClient() }
    override val chimpService by lazy { ChimpServiceHttp(API_BASE_URL, client) }
    override val sessionManager by lazy { SessionManagerPreferencesDataStore(preferencesDataStore) }
    override val entityReferenceManager by lazy { EntityReferenceManagerImpl() }
    override val storage by lazy { FireStoreStorage() }

    private fun createHttpClient() =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    },
                )
            }
        }

    override fun onCreate() {
        super.onCreate()
        createSSEWorker()
        createNotificationWorker()
    }

    override fun onTerminate() {
        super.onTerminate()
        WorkManager.getInstance(this).cancelAllWorkByTag(SSEWorker::class.java.name)
        WorkManager.getInstance(this).cancelAllWorkByTag(NotificationWorker::class.java.name)
    }

    private fun createSSEWorker() {
        val workManager = WorkManager.getInstance(this)
        val workItem =
            OneTimeWorkRequestBuilder<SSEWorker>()
                .addTag(SSEWorker::class.java.name)
                .build()
        workManager.beginUniqueWork(SSEWorker::class.java.name, ExistingWorkPolicy.REPLACE, workItem).enqueue()
    }

    private fun createNotificationWorker() {
        val workManager = WorkManager.getInstance(this)
        val workItem =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .addTag(NotificationWorker::class.java.name)
                .build()
        workManager.beginUniqueWork(NotificationWorker::class.java.name, ExistingWorkPolicy.REPLACE, workItem).enqueue()
    }

    companion object {
        /**
         * Tag used for logging.
         */
        const val TAG = "CHIMP_APPLICATION"

        private var instance: ChimpApplication? = null

        val isInitialized: Boolean
            get() = instance != null

        private val activityManager: ActivityManager by lazy {
            applicationContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        }

        val isInForeground: Boolean
            get() {
                val appProcesses = activityManager.runningAppProcesses ?: return false
                val packageName = applicationContext().packageName
                for (appProcess in appProcesses) {
                    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                        return true
                    }
                }
                return false
            }

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        private const val NGROK_TUNNEL = "together-kid-admittedly.ngrok-free.app"
        private const val API_BASE_URL = "https://$NGROK_TUNNEL/api"
    }
}
