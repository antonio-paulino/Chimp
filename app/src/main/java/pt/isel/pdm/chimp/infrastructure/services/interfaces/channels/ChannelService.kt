package pt.isel.pdm.chimp.infrastructure.services.interfaces.channels

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/**
 * Service that provides operations related to channels
 */
interface ChannelService {
    /**
     * Creates a new channel
     *
     * - The channel name must be unique
     * - The default role must be one of the following: [ChannelRole.MEMBER] or [ChannelRole.GUEST]
     *
     * @param name The name of the channel
     * @param defaultRole The default role of the channel
     * @param isPublic The visibility of the channel
     * @param session The session of the user
     *
     * @return Either a [Problem] or the created channel
     */
    suspend fun createChannel(
        name: Name,
        defaultRole: ChannelRole,
        isPublic: Boolean,
        session: Session,
    ): Either<Problem, Channel>

    /**
     * Gets the channel with the given identifier
     *
     * - The user must be a member of the channel to get it's details
     *
     * @param channelId The identifier of the channel
     * @param session The session of the user
     *
     * @return Either a [Problem] or the channel with the given identifier
     */
    suspend fun getChannel(
        channelId: Identifier,
        session: Session,
    ): Either<Problem, Channel>

    /**
     * Gets all the channels
     *
     * @param name The partial name of the channel
     * @param pagination The pagination information
     * @param sort The sort information
     * @param session The session of the user
     * @param after The identifier of the last channel currently in view
     *
     * @return Either a [Problem] or a list with all the channels
     */
    suspend fun getChannels(
        name: String?,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
        filterOwned: Boolean?,
    ): Either<Problem, Pagination<Channel>>

    /**
     * Updates the channel with the given identifier
     *
     * - The user must be the owner of the channel to update it
     *
     * @param channelId The identifier of the channel
     * @param name The new name of the channel
     * @param defaultRole The new default role of the channel
     * @param isPublic The new visibility of the channel
     * @param session The session of the user
     *
     * @return Either a [Problem] or success
     */
    suspend fun updateChannel(
        channelId: Identifier,
        name: Name,
        defaultRole: ChannelRole,
        isPublic: Boolean,
        session: Session,
    ): Either<Problem, Unit>

    /**
     * Deletes the channel with the given identifier
     *
     * - The user must be the owner of the channel to delete it
     *
     * @param channel The channel to delete
     * @param session The session of the user
     *
     * @return Either a [Problem] or success
     */
    suspend fun deleteChannel(
        channel: Channel,
        session: Session,
    ): Either<Problem, Unit>

    /**
     * Joins the channel with the given identifier
     *
     * - The user must not be in the channel to join it.
     * - The channel must be public to join it directly.
     *
     * @param channel The channel to join
     * @param session The session of the user
     *
     * @return Either a [Problem] or success
     */
    suspend fun joinChannel(
        channel: Channel,
        session: Session,
    ): Either<Problem, Unit>

    /**
     * Leaves the channel with the given identifier
     *
     * - The user must be in the channel to leave it.
     * - The user must not be the owner of the channel to leave it.
     * - The user must the owner of the channel or be removing themselves to remove another user.
     *
     * @param channel The channel to leave
     * @param user The user to remove
     * @param session The session of the user
     *
     * @return Either a [Problem] or success
     */
    suspend fun removeUserFromChannel(
        channel: Channel,
        user: User,
        session: Session,
    ): Either<Problem, Unit>

    /**
     * Updates the role of a user in the channel
     *
     * - An owner cannot change their role
     * - The user must be the owner of the channel to change the role of another user
     * - The role must be one of the following: [ChannelRole.MEMBER] or [ChannelRole.GUEST]
     *
     * @param channel The channel to update the role
     * @param user The user to update the role of
     * @param role The new role of the user
     * @param session The session of the user
     *
     * @return Either a [Problem] or success
     */
    suspend fun updateMemberRole(
        channel: Channel,
        user: User,
        role: ChannelRole,
        session: Session,
    ): Either<Problem, Unit>
}
