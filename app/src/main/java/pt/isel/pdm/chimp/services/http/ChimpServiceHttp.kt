package pt.isel.pdm.chimp.services.http

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.services.http.auth.AuthServiceHTTP
import pt.isel.pdm.chimp.services.interfaces.ChimpService
import pt.isel.pdm.chimp.services.interfaces.auth.AuthService
import pt.isel.pdm.chimp.services.interfaces.channels.ChannelService
import pt.isel.pdm.chimp.services.interfaces.invitations.InvitationService
import pt.isel.pdm.chimp.services.interfaces.messages.MessageService
import pt.isel.pdm.chimp.services.interfaces.users.UserService

class ChimpServiceHttp(
    baseUrl: String,
    httpClient: HttpClient,
) : ChimpService {
    override val authService: AuthService = AuthServiceHTTP(baseUrl, httpClient)
    override val userService: UserService = TODO()
    override val channelService: ChannelService = TODO()
    override val invitationService: InvitationService = TODO()
    override val messageService: MessageService = TODO()
}
