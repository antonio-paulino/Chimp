package pt.isel.pdm.chimp.infrastructure.services.http.events

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.header
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.events.EventService
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.awaitNetworkAvailable
import pt.isel.pdm.chimp.ui.utils.isNetworkAvailable
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

/**
 * HTTP implementation of the [EventService].
 *
 * This service is responsible for listening to the events from the server and updating the application's state accordingly.
 *
 * IMPORTANT: This service should only be invoked once in the application's lifecycle, and it should be destroyed when the application is closed.
 * It also should be created only when the application context is available, so it can access the network state to avoid unnecessary reconnect requests.
 *
 * @param httpClient The HTTP client to use for making requests.
 * @param baseUrl The base URL of the server.
 *
 */
class EventServiceHTTP(
    httpClient: HttpClient,
    baseUrl: String,
) : EventService, BaseHTTPService(httpClient, baseUrl) {
    private var job: Job? = null
    private val context: Context

    private var _eventFlow: Flow<Event>? = null

    init {
        check(ChimpApplication.isInitialized) {
            "Application context not initialized, make sure the application context is available before creating the service"
        }
        context = ChimpApplication.applicationContext()
    }

    override fun initialize(
        scope: CoroutineScope,
        session: SessionManager,
    ) {
        check(job == null) { "Event service already initialized" }
        job =
            scope.launch {
                _eventFlow = listenEvents(session, scope)
            }
        Log.d(TAG, "Event service initialized")
    }

    override suspend fun awaitInitialization(timeout: Duration) {
        val startTime = System.currentTimeMillis()
        while (_eventFlow == null) {
            if (System.currentTimeMillis() - startTime > timeout.inWholeMilliseconds) {
                throw TimeoutException("Initialization timed out")
            }
            delay(100)
        }
    }

    override fun destroy() {
        check(job != null) { "Event service not initialized" }
        job!!.cancel()
        job = null
    }

    override val eventFlow: Flow<Event> =
        flow {
            checkNotNull(_eventFlow) { "Event service not initialized" }
            _eventFlow!!.collect { event ->
                emit(event)
            }
        }

    override val channelEventFlow: Flow<Event.ChannelEvent> = eventFlow.filterIsInstance<Event.ChannelEvent>()

    override val invitationEventFlow: Flow<Event.InvitationEvent> = eventFlow.filterIsInstance<Event.InvitationEvent>()

    override val messageEventFlow: Flow<Event.MessageEvent> = eventFlow.filterIsInstance<Event.MessageEvent>()

    override fun getMessageEventsByChannel(channel: Channel): Flow<Event.MessageEvent> {
        return flow {
            messageEventFlow.collect { messageEvent ->
                when (messageEvent) {
                    is Event.MessageEvent.CreatedEvent -> {
                        if (messageEvent.message.channelId == channel.id) {
                            emit(messageEvent)
                        }
                    }
                    is Event.MessageEvent.UpdatedEvent -> {
                        if (messageEvent.message.channelId == channel.id) {
                            emit(messageEvent)
                        }
                    }
                    is Event.MessageEvent.DeletedEvent -> {
                        // Delete should have no effect on the channel's message flow
                        // if the message is not from the channel
                        emit(messageEvent)
                    }
                }
            }
        }
    }

    private fun listenEvents(
        session: SessionManager,
        scope: CoroutineScope,
    ): Flow<Event> {
        val url = "$baseUrl$EVENT_SOURCE_URL"
        var lastEventId: String? = null
        return flow {
            while (scope.isActive) {
                try {
                    Log.d(TAG, "Connecting to event source at $url")
                    httpClient.prepareRequest {
                        url(url)
                        header("Accept", "text/event-stream")
                        header("Authorization", "Bearer ${session.session.firstOrNull()?.accessToken?.token}")
                        lastEventId?.let { header("Last-Event-ID", it) }
                    }.execute { response ->
                        val channel = response.bodyAsChannel()
                        channel.readEvents(scope).collect { event ->
                            lastEventId = event.id
                            emit(event)
                        }
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "Error while reading events: ${e.message} - reconnecting in $reconnectTime")
                } catch (e: SocketException) {
                    Log.d(TAG, "Socket error: ${e.message} - reconnecting in $reconnectTime")
                } catch (e: ConnectException) {
                    Log.d(TAG, "Connection error: ${e.message} - reconnecting in $reconnectTime")
                } catch (e: ConnectTimeoutException) {
                    Log.d(TAG, "Connection timeout: ${e.message} - reconnecting in $reconnectTime")
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error: ${e.message} - reconnecting in $reconnectTime", e)
                }
                if (!context.isNetworkAvailable()) {
                    context.awaitNetworkAvailable()
                } else {
                    delay(reconnectTime)
                }
            }
        }
    }

    companion object {
        private const val EVENT_SOURCE_URL = "/sse/listen"
        private val reconnectTime = Duration.parse("PT5S")
    }
}

// fun main() {
//    runBlocking {
//        val httpClient = HttpClient(OkHttp) {
//            install(ContentNegotiation) {
//                json(
//                    Json {
//                        ignoreUnknownKeys = true
//                        prettyPrint = true
//                        isLenient = true
//                    }
//                )
//            }
//        }
//
//        val baseUrl = "http://localhost:8080/api"
//        val eventService = EventServiceHTTP(httpClient, baseUrl)
//        val authService = AuthServiceHTTP(baseUrl, httpClient)
//        val scope = CoroutineScope(Job() + Dispatchers.IO)
//
//        var session: Session? = null
//
//
//        scope.launch {
//            session = (authService.login("Instant Messaging", null, "Iseldaw-g07") as Success).value
//            eventService.startListening(scope, session!!)
//
//
//            launch {
//                eventService.getMessages().collect { messages ->
//                    println("messages size = ${messages.size}")
//                }
//            }
//
//            launch {
//                eventService.getInvitations().collect { invitations ->
//                    println("invitations size = ${invitations.size}")
//                }
//            }
//        }
//
//        var isActivated = true
//        while (isActivated) {
//            val input = readlnOrNull()
//            if (input == "exit") {
//                isActivated = false
//
//                // Logout and stop listening
//                session?.let {
//                    authService.logout(it)
//                    eventService.stopListening(scope)
//                }
//
//                scope.cancel()
//
//                scope.coroutineContext[Job]?.join()
//
//                httpClient.close()
//
//                println("Application closed.")
//            }
//        }
//    }
//    println("Main coroutine finished.")
// }
