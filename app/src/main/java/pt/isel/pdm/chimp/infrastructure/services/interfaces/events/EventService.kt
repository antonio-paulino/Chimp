package pt.isel.pdm.chimp.infrastructure.services.interfaces.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event

/**
 * Represents the service responsible for managing the events of the Chelas Instant Messaging Platform.
 */
interface EventService {
    /**
     * Global flow of events received from the server.
     *
     * This flow should be used to listen to all the events received from the server.
     *
     * @throws IllegalStateException If the service is not initialized.
     */
    val eventFlow: Flow<Event>

    /**
     * Flow of channel events received from the server.
     *
     * This flow should be used to listen to all the channel events received from the server.
     *
     * @throws IllegalStateException If the service is not initialized.
     */
    val channelEventFlow: Flow<Event.ChannelEvent>

    /**
     * Flow of invitation events received from the server.
     *
     * This flow should be used to listen to all the invitation events received from the server.
     *
     * @throws IllegalStateException If the service is not initialized.
     */
    val invitationEventFlow: Flow<Event.InvitationEvent>

    /**
     * Flow of message events received from the server.
     *
     * This flow should be used to listen to all the message events received from the server.
     *
     * @throws IllegalStateException If the service is not initialized.
     */
    val messageEventFlow: Flow<Event.MessageEvent>

    /**
     * Returns a flow of message events from a specific channel.
     *
     * @param channel The channel to get the messages from.
     *
     * @throws IllegalStateException If the service is not initialized.
     */
    fun getMessageEventsByChannel(channel: Channel): Flow<Event.MessageEvent>

    /**
     * Initializes the service to start listening to the events from the server.
     *
     * This method should be called only once, ideally in the application's
     * in the main activitý's lifecycle scope.
     *
     * Before adding any listeners, the service should be initialized,
     * or an exception will be thrown.
     *
     * @throws IllegalStateException If the service is already initialized.
     *
     * @param scope The coroutine scope to use for listening to the events.
     * @param session The session to use for establishing the connection.
     */
    fun initialize(
        scope: CoroutineScope,
        session: Session,
    )

    /**
     * Stops listening to events.
     *
     * @throws IllegalStateException If the service is not initialized.
     */
    fun destroy()
}
