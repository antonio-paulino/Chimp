package pt.isel.pdm.chimp

import android.app.Application
import android.content.Context
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
import pt.isel.pdm.chimp.infrastructure.session.SessionManagerMem

/**
 * Represents the dependencies container for the Chimp application.
 */
interface DependenciesContainer {
    val chimpService: ChimpService
    val sessionManager: SessionManager
    val entityReferenceManager: EntityReferenceManager
}

class ChimpApplication : Application(), DependenciesContainer {

    init {
        instance = this
    }

    private val client by lazy { createHttpClient() }
    override val chimpService by lazy { ChimpServiceHttp(API_BASE_URL, client) }
    override val sessionManager by lazy { SessionManagerMem() } // TODO: Use a persistent implementation of the session manager
    override val entityReferenceManager by lazy { EntityReferenceManagerImpl() }

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

    companion object {
        /**
         * Tag used for logging.
         */
        const val TAG = "CHIMP_APPLICATION"

        private var instance: ChimpApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        private const val API_BASE_URL = "http://10.0.2.2:8080/api"
    }

}
