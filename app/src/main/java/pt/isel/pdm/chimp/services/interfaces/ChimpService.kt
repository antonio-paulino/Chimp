package pt.isel.pdm.chimp.services.interfaces

import pt.isel.pdm.chimp.services.interfaces.auth.AuthService
import pt.isel.pdm.chimp.services.interfaces.channels.ChannelService
import pt.isel.pdm.chimp.services.interfaces.invitations.InvitationService
import pt.isel.pdm.chimp.services.interfaces.messages.MessageService
import pt.isel.pdm.chimp.services.interfaces.users.UserService

/**
 * Represents the services container for the Chelas Instant Messaging Platform.
 */
interface ChimpService {
    val authService: AuthService
    val userService: UserService
    val channelService: ChannelService
    val messageService: MessageService
    val invitationService: InvitationService
}
