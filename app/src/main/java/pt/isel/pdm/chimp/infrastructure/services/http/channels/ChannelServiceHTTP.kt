package pt.isel.pdm.chimp.infrastructure.services.http.channels

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.dto.input.ChannelCreationInputModel
import pt.isel.pdm.chimp.dto.input.ChannelRoleUpdateInputModel
import pt.isel.pdm.chimp.dto.output.channel.ChannelCreationOutputModel
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.dto.output.channel.ChannelsPaginatedOutputModel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNELS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_MEMBERS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.USER_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.buildQuery
import pt.isel.pdm.chimp.infrastructure.services.http.handle
import pt.isel.pdm.chimp.infrastructure.services.interfaces.channels.ChannelService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/**
 * HTTP implementation of the [ChannelService].
 *
 * @property baseURL the base URL of the service
 * @property httpClient the HTTP client
 */
class ChannelServiceHTTP(
    baseURL: String,
    httpClient: HttpClient,
) : BaseHTTPService(httpClient, baseURL), ChannelService {
    /**
     * The implementation of the [ChannelService.createChannel] method.
     *
     * @param name The name of the channel.
     * @param defaultRole The default role of the channel.
     * @param isPublic Whether the channel is public or not.
     * @param session The session of the user creating the channel.
     *
     * @return An [Either] containing the [Channel] if the creation was successful or a [Problem] if it was not.
     */
    override suspend fun createChannel(
        name: Name,
        defaultRole: ChannelRole,
        isPublic: Boolean,
        session: Session,
    ): Either<Problem, Channel> {
        return post<ChannelCreationInputModel, ChannelCreationOutputModel>(
            CHANNELS_ROUTE,
            session.accessToken.token.toString(),
            ChannelCreationInputModel(name, defaultRole, isPublic),
        ).handle {
            it.toDomain(name, defaultRole, isPublic, session.user)
        }
    }

    /**
     * The implementation of the [ChannelService.getChannel] method.
     *
     * @param channelId The identifier of the channel.
     * @param session The session of the user getting the channel.
     *
     * @return An [Either] containing the [Channel] if the retrieval was successful or a [Problem] if it was not.
     */
    override suspend fun getChannel(
        channelId: Identifier,
        session: Session,
    ): Either<Problem, Channel> {
        return get<ChannelOutputModel>(
            CHANNEL_ROUTE
                .replace(CHANNEL_ID_PARAM, channelId.value.toString()),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }

    /**
     * The implementation of the [ChannelService.getChannels] method.
     *
     * @param name The name of the channels to retrieve.
     * @param session The session of the user getting the channels.
     * @param pagination The pagination request.
     * @param sort The sort request.
     * @param after The identifier of the last channel retrieved.
     *
     * @return An [Either] containing the [Pagination] of [Channel]s if the retrieval was successful or a [Problem] if it was not.
     */
    override suspend fun getChannels(
        name: String?,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
    ): Either<Problem, Pagination<Channel>> {
        return get<ChannelsPaginatedOutputModel>(
            CHANNELS_ROUTE + buildQuery(name, pagination, sort, after = after),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }

    /**
     * The implementation of the [ChannelService.updateChannel] method.
     *
     * @param channelId The identifier of the channel.
     * @param name The new name of the channel.
     * @param defaultRole The new default role of the channel.
     * @param isPublic Whether the channel is public or not.
     * @param session The session of the user updating the channel.
     *
     * @return An [Either] containing [Unit] if the update was successful or a [Problem] if it was not.
     */
    override suspend fun updateChannel(
        channelId: Identifier,
        name: Name,
        defaultRole: ChannelRole,
        isPublic: Boolean,
        session: Session,
    ): Either<Problem, Unit> {
        return put<ChannelCreationInputModel, Unit>(
            CHANNEL_ROUTE
                .replace(CHANNEL_ID_PARAM, channelId.value.toString()),
            session.accessToken.token.toString(),
            ChannelCreationInputModel(name, defaultRole, isPublic),
        ).handle { }
    }

    /**
     * The implementation of the [ChannelService.deleteChannel] method.
     *
     * @param channel The channel to delete.
     * @param session The session of the user deleting the channel.
     *
     * @return An [Either] containing [Unit] if the deletion was successful or a [Problem] if it was not.
     */
    override suspend fun deleteChannel(
        channel: Channel,
        session: Session,
    ): Either<Problem, Unit> {
        return delete(
            CHANNEL_ROUTE.replace(
                CHANNEL_ID_PARAM,
                channel.id.value.toString(),
            ),
            session.accessToken.token.toString(),
        ).handle { }
    }

    /**
     * The implementation of the [ChannelService.joinChannel] method.
     *
     * @param channel The channel to join.
     * @param session The session of the user joining the channel.
     *
     * @return An [Either] containing [Unit] if the join was successful or a [Problem] if it was not.
     */
    override suspend fun joinChannel(
        channel: Channel,
        session: Session,
    ): Either<Problem, Unit> {
        return put<Unit, Unit>(
            CHANNEL_MEMBERS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(USER_ID_PARAM, session.user.id.value.toString()),
            session.accessToken.token.toString(),
            null,
        ).handle { }
    }

    /**
     * The implementation of the [ChannelService.removeMemberFromChannel] method.
     *
     * @param channel The channel to leave.
     * @param session The session of the user leaving the channel.
     *
     * @return An [Either] containing [Unit] if the leave was successful or a [Problem] if it was not.
     */
    override suspend fun removeMemberFromChannel(
        channel: Channel,
        member: ChannelMember,
        session: Session,
    ): Either<Problem, Unit> {
        return delete(
            CHANNEL_MEMBERS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(USER_ID_PARAM, member.id.value.toString()),
            session.accessToken.token.toString(),
        ).handle { }
    }

    /**
     * The implementation of the [ChannelService.updateMemberRole] method.
     *
     * @param channel The channel to update the role of the user.
     * @param member The member to update the role.
     * @param role The new role of the user.
     * @param session The session of the user updating the role.
     *
     * @return An [Either] containing [Unit] if the update was successful or a [Problem] if it was not.
     */
    override suspend fun updateMemberRole(
        channel: Channel,
        member: ChannelMember,
        role: ChannelRole,
        session: Session,
    ): Either<Problem, Unit> {
        return patch<ChannelRoleUpdateInputModel>(
            CHANNEL_MEMBERS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(USER_ID_PARAM, member.id.value.toString()),
            session.accessToken.token.toString(),
            ChannelRoleUpdateInputModel(role),
        ).handle { }
    }
}
