package pt.isel.pdm.chimp.infrastructure.services.http.events

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.events.EventService
import pt.isel.pdm.chimp.ui.utils.awaitNetworkAvailable
import pt.isel.pdm.chimp.ui.utils.isNetworkAvailable
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import kotlin.time.Duration

class EventServiceHTTP(
    httpClient: HttpClient,
    baseUrl: String,
) : EventService, BaseHTTPService(httpClient, baseUrl) {

    private val messageFlow = MutableStateFlow<List<Message>>(emptyList())
    private val invitationFlow = MutableStateFlow<List<ChannelInvitation>>(emptyList())
    private val channelFlow = MutableStateFlow<List<Channel>>(emptyList())

    private val scopeListeners = mutableMapOf<CoroutineScope, List<Job>>()

    private val context = ChimpApplication.applicationContext()

    override fun startListening(scope: CoroutineScope, session: Session) {
        val job = scope.launch {
            try {
                listenEvents(session, scope)
            } catch (e: ConnectException) {
                Log.d(TAG, "Connection error: ${e.message}")
            }
        }
        val listeners = scopeListeners.getOrPut(scope) { emptyList() }
        scopeListeners[scope] = listeners + job
    }

    override fun stopListening(scope: CoroutineScope) {
        scopeListeners[scope]?.forEach { it.cancel() }
        scopeListeners.remove(scope)
    }

    private fun handleEvent(event: Event) {
        println("Received event: $event")
        when (event) {
            is Event.MessageCreatedEvent -> messageFlow.update { it + event.message }
            is Event.MessageDeletedEvent -> messageFlow.update { it.filter { message -> message.id != event.messageId } }
            is Event.MessageUpdatedEvent -> messageFlow.update { it.map { message -> if (message.id == event.message.id) event.message else message } }
            is Event.InvitationCreatedEvent -> invitationFlow.update { it + event.invitation }
            is Event.InvitationDeletedEvent -> invitationFlow.update { it.filter { invitation -> invitation.id != event.invitationId } }
            is Event.InvitationUpdatedEvent -> invitationFlow.update { it.map { invitation -> if (invitation.id == event.invitation.id) event.invitation else invitation } }
            is Event.ChannelDeletedEvent -> channelFlow.update { it.filter { channel -> channel.id != event.channelId } }
            is Event.ChannelUpdatedEvent -> channelFlow.update { it.map { channel -> if (channel.id == event.channel.id) event.channel else channel } }
            is Event.KeepAliveEvent -> { /* Do nothing */ }
        }
    }

    private suspend fun listenEvents(session: Session, scope: CoroutineScope) {
        val url = "$baseUrl$EVENT_SOURCE_URL"
        var lastEventId: String? = null
        while (scope.isActive) {
            try {
                Log.d(TAG, "Connecting to event source at $url")
                httpClient.prepareRequest {
                    url(url)
                    header("Accept", "text/event-stream")
                    header("Authorization", "Bearer ${session.accessToken.token}")
                    lastEventId?.let { header("Last-Event-ID", it) }
                }.execute { response ->
                    val channel = response.bodyAsChannel()
                    channel.readEvents(scope).collect { event ->
                        Log.d(TAG, "Received event: $event")
                        lastEventId = event.id
                        handleEvent(event)
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
            }
            if (!context.isNetworkAvailable()) {
                 context.awaitNetworkAvailable()
             }  else {
                 delay(reconnectTime)
             }
        }
    }

    override suspend fun getMessages(): Flow<List<Message>> = messageFlow

    override suspend fun getInvitations(): Flow<List<ChannelInvitation>> = invitationFlow

    override suspend fun getChannels(): Flow<List<Channel>> = channelFlow

    override fun addMessagesToFlow(messages: List<Message>, prepend: Boolean) {
        messageFlow.update { if (prepend) messages + it else it + messages }
    }

    override fun addInvitationsToFlow(invitations: List<ChannelInvitation>) {
        invitationFlow.update { it + invitations }
    }

    override fun addChannelsToFlow(channels: List<Channel>) {
        channelFlow.update { it + channels }
    }

    companion object {
        private const val EVENT_SOURCE_URL = "/sse/listen"
        private val reconnectTime = Duration.parse("PT5S")
    }
}


//fun main() {
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
//}