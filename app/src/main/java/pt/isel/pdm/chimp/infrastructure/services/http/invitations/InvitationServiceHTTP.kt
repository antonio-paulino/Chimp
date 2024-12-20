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
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_INVITATIONS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_INVITATION_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.INVITE_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.USER_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.USER_INVITATIONS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.USER_INVITATION_ROUTE
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
    /**
     * The implementation of the [InvitationService.createChannelInvitation] method.
     *
     * @param channel The channel to create the invitation for.
     * @param invitee The user to invite.
     * @param expiresAt The expiration date of the invitation.
     * @param role The role of the invitee.
     * @param session The session of the user creating the invitation.
     *
     * @return An [Either] containing the [ChannelInvitation] if the creation was successful or a [Problem] if it was not.
     */
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

    /**
     * The implementation of the [InvitationService.getInvitation] method.
     *
     * @param channel The channel of the invitation.
     * @param inviteId The identifier of the invitation.
     * @param session The session of the user getting the invitation.
     *
     * @return An [Either] containing the [ChannelInvitation] if the retrieval was successful or a [Problem] if it was not.
     */
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

    /**
     * The implementation of the [InvitationService.getChannelInvitations] method.
     *
     * @param channel The channel to get the invitations from.
     * @param session The session of the user getting the invitations.
     * @param pagination The pagination request.
     * @param sort The sort request.
     * @param after The identifier to get the invitations after.
     *
     * @return An [Either] containing the [Pagination] of [ChannelInvitation] if the retrieval was successful or a [Problem] if it was not.
     */
    override suspend fun getChannelInvitations(
        channel: Channel,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
    ): Either<Problem, Pagination<ChannelInvitation>> =
        get<ChannelInvitationsPaginatedOutputModel>(
            CHANNEL_INVITATIONS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .plus(buildQuery(null, pagination, sort, after = after)),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }

    /**
     * The implementation of the [InvitationService.updateInvitation] method.
     *
     *  @param invitation The invitation to update.
     *  @param role The new role of the invitee.
     *  @param expiresAt The new expiration date of the invitation.
     *  @param session The session of the user updating the invitation.
     */
    override suspend fun updateInvitation(
        invitation: ChannelInvitation,
        role: ChannelRole?,
        expiresAt: LocalDateTime?,
        session: Session,
    ): Either<Problem, Unit> =
        patch<ChannelInvitationUpdateInputModel>(
            CHANNEL_INVITATION_ROUTE
                .replace(CHANNEL_ID_PARAM, invitation.channel.id.value.toString())
                .replace(INVITE_ID_PARAM, invitation.id.value.toString()),
            session.accessToken.token.toString(),
            ChannelInvitationUpdateInputModel(role, expiresAt),
        ).handle { }

    override suspend fun deleteInvitation(
        invitation: ChannelInvitation,
        session: Session,
    ): Either<Problem, Unit> {
        return delete(
            CHANNEL_INVITATION_ROUTE
                .replace(CHANNEL_ID_PARAM, invitation.channel.id.value.toString())
                .replace(INVITE_ID_PARAM, invitation.id.value.toString()),
            session.accessToken.token.toString(),
        ).handle { }
    }

    override suspend fun getUserInvitations(
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
    ): Either<Problem, Pagination<ChannelInvitation>> {
        return get<ChannelInvitationsPaginatedOutputModel>(
            USER_INVITATIONS_ROUTE
                .replace(USER_ID_PARAM, session.user.id.value.toString())
                .plus(buildQuery(null, pagination, sort, after = after)),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }

    override suspend fun acceptOrRejectInvitation(
        channel: Channel,
        invitation: ChannelInvitation,
        accept: Boolean,
        session: Session,
    ): Either<Problem, Unit> {
        return patch<InvitationAcceptInputModel>(
            USER_INVITATION_ROUTE
                .replace(USER_ID_PARAM, session.user.id.value.toString())
                .replace(INVITE_ID_PARAM, invitation.id.value.toString()),
            session.accessToken.token.toString(),
            InvitationAcceptInputModel(accept),
        ).handle { }
    }
}
