package pt.isel.pdm.chimp.infrastructure.services.http.invitations

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.dto.input.ChannelInvitationCreationInputModel
import pt.isel.pdm.chimp.dto.input.ChannelInvitationUpdateInputModel
import pt.isel.pdm.chimp.dto.input.InvitationAcceptInputModel
import pt.isel.pdm.chimp.dto.output.invitations.ChannelInvitationCreationOutputModel
import pt.isel.pdm.chimp.dto.output.invitations.ChannelInvitationOutputModel
import pt.isel.pdm.chimp.dto.output.invitations.ChannelInvitationsPaginatedOutputModel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.http.buildQuery
import pt.isel.pdm.chimp.infrastructure.services.http.handle
import pt.isel.pdm.chimp.infrastructure.services.interfaces.invitations.InvitationService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import java.time.LocalDateTime

/**
 * HTTP implementation of the [InvitationService].
 */
class InvitationServiceHTTP(baseURL: String, httpClient: HttpClient) :
    BaseHTTPService(httpClient, baseURL), InvitationService {
    override suspend fun createChannelInvitation(
        channel: Channel,
        invitee: User,
        expiresAt: LocalDateTime,
        role: ChannelRole,
        session: Session,
    ): Either<Problem, ChannelInvitation> =
        post<ChannelInvitationCreationInputModel, ChannelInvitationCreationOutputModel>(
            CHANNEL_INVITATIONS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString()),
            session.accessToken.token.toString(),
            ChannelInvitationCreationInputModel(invitee.id, expiresAt, role),
        ).handle { it.toDomain(channel, session.user, invitee, role, expiresAt) }

    override suspend fun getInvitation(
        channel: Channel,
        inviteId: Identifier,
        session: Session,
    ): Either<Problem, ChannelInvitation> {
        return get<ChannelInvitationOutputModel>(
            CHANNEL_INVITATION_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(INVITE_ID_PARAM, inviteId.value.toString()),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }

    override suspend fun getChannelInvitations(
        channel: Channel,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<ChannelInvitation>> =
        get<ChannelInvitationsPaginatedOutputModel>(
            CHANNEL_INVITATIONS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .plus(buildQuery(null, pagination, sort)),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }

    override suspend fun updateInvitation(
        invitationId: Identifier,
        role: ChannelRole,
        expiresAt: LocalDateTime,
        session: Session,
    ): Either<Problem, Unit> =
        patch<ChannelInvitationUpdateInputModel, Unit>(
            CHANNEL_INVITATION_ROUTE
                .replace(CHANNEL_ID_PARAM, invitationId.value.toString())
                .replace(INVITE_ID_PARAM, invitationId.value.toString()),
            session.accessToken.token.toString(),
            ChannelInvitationUpdateInputModel(role, expiresAt),
        ).handle { }

    override suspend fun deleteInvitation(
        channel: Channel,
        invitation: ChannelInvitation,
        session: Session,
    ): Either<Problem, Unit> {
        return delete(
            CHANNEL_INVITATION_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(INVITE_ID_PARAM, invitation.id.value.toString()),
            session.accessToken.token.toString(),
        ).handle { }
    }

    override suspend fun getUserInvitations(
        user: User,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<ChannelInvitation>> {
        return get<ChannelInvitationsPaginatedOutputModel>(
            USER_INVITATIONS_ROUTE
                .replace(USER_ID_PARAM, user.id.value.toString())
                .plus(buildQuery(null, pagination, sort)),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }

    override suspend fun acceptOrRejectInvitation(
        channel: Channel,
        invitation: ChannelInvitation,
        accept: Boolean,
        session: Session,
    ): Either<Problem, Unit> {
        return patch<InvitationAcceptInputModel, Unit>(
            USER_INVITATION_ROUTE
                .replace(USER_ID_PARAM, session.user.id.value.toString())
                .replace(INVITE_ID_PARAM, invitation.id.value.toString()),
            session.accessToken.token.toString(),
            InvitationAcceptInputModel(accept),
        ).handle { }
    }

    companion object {
        private const val CHANNEL_ID_PARAM = "{channelId}"
        private const val INVITE_ID_PARAM = "{inviteId}"
        private const val USER_ID_PARAM = "{userId}"
        private const val CHANNEL_INVITATIONS_ROUTE = "channels/$CHANNEL_ID_PARAM/invitations"
        private const val CHANNEL_INVITATION_ROUTE =
            "channels/$CHANNEL_ID_PARAM/invitations/$INVITE_ID_PARAM"
        private const val USER_INVITATIONS_ROUTE = "users/$USER_ID_PARAM/invitations"
        private const val USER_INVITATION_ROUTE =
            "users/$USER_ID_PARAM/invitations/$INVITE_ID_PARAM"
    }
}
