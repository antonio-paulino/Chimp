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
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private var _eventFlow: MutableSharedFlow<Event>? = null

    init {
        check(ChimpApplication.isInitialized) {
            "Application context not initialized, make sure the application context is available before creating the service"
        }
        context = ChimpApplication.applicationContext()
    }

    /**
     * Initializes the event service.
     *
     * This method should be called only once in the application's lifecycle,
     * to start listening to the events from the server.
     *
     * @param scope The scope to use for the service.
     * @param session The session of the user.
     */
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

    /**
     * Awaits the initialization of the event service.
     *
     * @param timeout The maximum time to wait for the initialization.
     */
    override suspend fun awaitInitialization(timeout: Duration) {
        val startTime = System.currentTimeMillis()
        while (_eventFlow == null) {
            if (System.currentTimeMillis() - startTime > timeout.inWholeMilliseconds) {
                throw TimeoutException("Initialization timed out")
            }
            delay(100)
        }
    }

    /**
     * Destroys the event service.
     *
     * This method should be called only once in the application's lifecycle,
     * to stop listening to the events from the server, when the application is closed.
     */
    override fun destroy() {
        check(job != null) { "Event service not initialized" }
        job!!.cancel()
        job = null
    }

    /**
     * The flow of events received from the server.
     */
    override val eventFlow: Flow<Event> =
        flow {
            checkNotNull(_eventFlow) { "Event service not initialized" }
            _eventFlow!!.collect { event ->
                emit(event)
            }
        }

    /**
     * The flow of channel events received from the server.
     */
    override val channelEventFlow: Flow<Event.ChannelEvent> = eventFlow.filterIsInstance<Event.ChannelEvent>()

    /**
     * The flow of invitation events received from the server.
     */
    override val invitationEventFlow: Flow<Event.InvitationEvent> = eventFlow.filterIsInstance<Event.InvitationEvent>()

    /**
     * The flow of message events received from the server.
     */
    override val messageEventFlow: Flow<Event.MessageEvent> = eventFlow.filterIsInstance<Event.MessageEvent>()

    /**
     * Gets the message events by channel.
     *
     * @param channel The channel to get the message events from.
     *
     * @return The flow of message events from the channel.
     */
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
                        emit(messageEvent)
                    }
                }
            }
        }
    }

    /**
     * Starts listening to the events from the server.
     *
     * This function establishes an event source connection with the server and listens to the events.
     *
     * Whenever a connection error occurs, this method will attempt to reconnect after a delay.
     *
     * @param scope The scope to use for the service.
     * @param session The session of the user.
     */
    private fun listenEvents(
        session: SessionManager,
        scope: CoroutineScope,
    ): MutableSharedFlow<Event> {
        val sharedFlow = MutableSharedFlow<Event>(replay = 0)
        val url = "$baseUrl$EVENT_SOURCE_URL"
        var lastEventId: String? = null
        scope.launch {
            while (scope.isActive) {
                try {
                    httpClient.prepareRequest {
                        url(url)
                        header("Accept", "text/event-stream")
                        header("Authorization", "Bearer ${session.session.firstOrNull()?.accessToken?.token}")
                        lastEventId?.let { header("Last-Event-ID", it) }
                    }.execute { response ->
                        val channel = response.bodyAsChannel()
                        channel.readEvents(scope).collect { event ->
                            lastEventId = event.id
                            sharedFlow.emit(event)
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
                } finally {
                    if (!context.isNetworkAvailable()) {
                        context.awaitNetworkAvailable()
                    } else {
                        delay(reconnectTime)
                    }
                }
            }
        }
        return sharedFlow
    }

    companion object {
        private const val EVENT_SOURCE_URL = "/sse/listen"
        private val reconnectTime = Duration.parse("PT0.5S")
    }
}
