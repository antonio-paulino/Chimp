package pt.isel.pdm.chimp.infrastructure.services.interfaces.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.sessions.Session

/**
 * Represents the service responsible for managing the events of the Chelas Instant Messaging Platform.
 */
interface EventService {
    /**
     * Starts listening to the events from the server.
     *
     * The [scope] parameter is used to control the lifecycle of the listening process.
     * The [session] parameter is used to authenticate the user.
     */
    fun startListening(scope: CoroutineScope, session: Session)

    /**
     * Stops listening to the events from the server.
     */
    fun stopListening(scope: CoroutineScope)

    /**
     * Returns a flow of messages that represents the messages received from the server.
     */
    suspend fun getMessages() : Flow<List<Message>>

    /**
     * Returns a flow of invitations that represents the invitations received from the server.
     */
    suspend fun getInvitations() : Flow<List<ChannelInvitation>>

    /**
     * Returns a flow of channels that represents the channels received from the server.
     */
    suspend fun getChannels() : Flow<List<Channel>>

    /**
     * Adds the specified [messages] to the flow of messages.
     *
     * If [prepend] is true, the messages will be added to the beginning of the flow.
     */
    fun addMessagesToFlow(messages: List<Message>, prepend: Boolean = false)

    /**
     * Adds the specified [invitations] to the flow of invitations.
     */
    fun addInvitationsToFlow(invitations: List<ChannelInvitation>)

    /**
     * Adds the specified [channels] to the flow of channels.
     */
    fun addChannelsToFlow(channels: List<Channel>)
}