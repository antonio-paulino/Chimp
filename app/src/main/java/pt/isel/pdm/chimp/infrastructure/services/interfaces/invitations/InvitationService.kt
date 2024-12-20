package pt.isel.pdm.chimp.infrastructure.services.interfaces.invitations

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
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import java.time.LocalDateTime

/**
 * Service that provides operations related to invitations.
 */
interface InvitationService {
    /**
     * Creates an invitation for a user to join a channel.
     *
     * - The inviter must be the owner of the channel.
     * - The invitee must not be a member of the channel.
     * - The expiration date must be in the future.
     * - The role must be one of the following: [ChannelRole.MEMBER] or [ChannelRole.GUEST].
     *
     * @param invitee The user to invite.
     * @param expiresAt The expiration date of the invitation.
     * @param role The role that the user will have in the channel.
     * @param session The session of the inviter.
     */
    suspend fun createChannelInvitation(
        channel: Channel,
        invitee: User,
        expiresAt: LocalDateTime,
        role: ChannelRole,
        session: Session,
    ): Either<Problem, ChannelInvitation>

    /**
     * Gets an invitation for a user to join a channel.
     *
     * - The user must be the inviter or invitee to see the invitation.
     *
     * @param channel The channel of the invitation.
     * @param inviteId The identifier of the invitation.
     * @param session The session of the user.
     *
     * @return Either a [Problem] or the invitation with the given identifier.
     */
    suspend fun getInvitation(
        channel: Channel,
        inviteId: Identifier,
        session: Session,
    ): Either<Problem, ChannelInvitation>

    /**
     * Gets all the invitations for a channel.
     *
     * - The user must be the owner of the channel to see the invitations.
     *
     * @param channel The channel to get the invitations.
     * @param pagination The pagination information.
     * @param sort The sort information.
     * @param session The session of the user.
     * @param after The identifier of the last invitation currently in view.
     *
     * @return Either a [Problem] or a list with all the invitations.
     */
    suspend fun getChannelInvitations(
        channel: Channel,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
    ): Either<Problem, Pagination<ChannelInvitation>>

    /**
     * Updates an invitation for a user to join a channel.
     *
     * - The user must be the inviter to update the invitation.
     *
     * @param invitation The invitation to update.
     * @param role The role that the user will have in the channel.
     * @param expiresAt The expiration date of the invitation.
     * @param session The session of the user.
     *
     * @return Either a [Problem] or the updated invitation.
     */
    suspend fun updateInvitation(
        invitation: ChannelInvitation,
        role: ChannelRole?,
        expiresAt: LocalDateTime?,
        session: Session,
    ): Either<Problem, Unit>

    /**
     * Deletes an invitation for a user to join a channel.
     *
     * - The user must be the inviter to delete the invitation.
     *
     * @param invitation The invitation to delete.
     * @param session The session of the user.
     */
    suspend fun deleteInvitation(
        invitation: ChannelInvitation,
        session: Session,
    ): Either<Problem, Unit>

    /**
     * Gets the invitations for the currently authenticated user.
     *
     * @param pagination The pagination information.
     * @param sort The sort information.
     * @param session The session of the user.
     * @param after The identifier of the last invitation currently in view.
     *
     * @return Either a [Problem] or a list with all the invitations.
     */
    suspend fun getUserInvitations(
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
    ): Either<Problem, Pagination<ChannelInvitation>>

    /**
     * Accepts or rejects an invitation for a user to join a channel.
     *
     * - The user must be the invitee to accept the invitation.
     *
     * @param channel The channel of the invitation.
     * @param invitation The invitation to accept or reject.
     * @param accept The decision to accept or reject the invitation.
     * @param session The session of the user.
     */
    suspend fun acceptOrRejectInvitation(
        channel: Channel,
        invitation: ChannelInvitation,
        accept: Boolean,
        session: Session,
    ): Either<Problem, Unit>
}
