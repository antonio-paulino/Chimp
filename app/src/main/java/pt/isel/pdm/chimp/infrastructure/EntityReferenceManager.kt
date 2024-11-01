package pt.isel.pdm.chimp.infrastructure

import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.user.User

/**
 * Interface that defines the operations to manage the references to the entities that are being
 * manipulated by the application's views.
 *
 * Since the application can have multiple activities, each with its own view model, this interface
 * is used to share the state of selected entities between the different view models.
 *
 * E.g. when a user selects a channel in the channels screen, navigation occurs to the
 * specific channel screen, and the channel reference is passed to the channel view model
 * through this manager.
 */
interface EntityReferenceManager {
    val message: Message?
    val channel: Channel?
    val user: User?
    val invitation: ChannelInvitation?

    fun set(message: Message)

    fun set(channel: Channel)

    fun set(user: User)

    fun set(invitation: ChannelInvitation)
}
