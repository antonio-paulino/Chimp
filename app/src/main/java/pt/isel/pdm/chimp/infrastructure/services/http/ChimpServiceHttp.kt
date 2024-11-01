package pt.isel.pdm.chimp.infrastructure.services.http

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.infrastructure.services.http.auth.AuthServiceHTTP
import pt.isel.pdm.chimp.infrastructure.services.http.channels.ChannelServiceHTTP
import pt.isel.pdm.chimp.infrastructure.services.http.invitations.InvitationServiceHTTP
import pt.isel.pdm.chimp.infrastructure.services.http.messages.MessageServiceHTTP
import pt.isel.pdm.chimp.infrastructure.services.http.users.UserServiceHTTP
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.auth.AuthService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.channels.ChannelService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.invitations.InvitationService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.messages.MessageService
import pt.isel.pdm.chimp.infrastructure.services.interfaces.users.UserService

class ChimpServiceHttp(
    baseUrl: String,
    httpClient: HttpClient,
) : ChimpService {
    override val authService: AuthService = AuthServiceHTTP(baseUrl, httpClient)
    override val userService: UserService = UserServiceHTTP(baseUrl, httpClient)
    override val channelService: ChannelService = ChannelServiceHTTP(baseUrl, httpClient)
    override val invitationService: InvitationService = InvitationServiceHTTP(baseUrl, httpClient)
    override val messageService: MessageService = MessageServiceHTTP(baseUrl, httpClient)
}
