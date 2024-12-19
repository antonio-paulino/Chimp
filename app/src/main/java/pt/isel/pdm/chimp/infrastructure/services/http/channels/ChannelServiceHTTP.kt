package pt.isel.pdm.chimp.infrastructure.services.http.channels

import io.ktor.client.HttpClient
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
 */
class ChannelServiceHTTP(
    baseURL: String,
    httpClient: HttpClient,
) : BaseHTTPService(httpClient, baseURL), ChannelService {
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

    override suspend fun removeUserFromChannel(
        channel: Channel,
        user: User,
        session: Session,
    ): Either<Problem, Unit> {
        return delete(
            CHANNEL_MEMBERS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(USER_ID_PARAM, user.id.value.toString()),
            session.accessToken.token.toString(),
        ).handle { }
    }

    override suspend fun updateMemberRole(
        channel: Channel,
        user: User,
        role: ChannelRole,
        session: Session,
    ): Either<Problem, Unit> {
        return patch<ChannelRoleUpdateInputModel>(
            CHANNEL_MEMBERS_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(USER_ID_PARAM, user.id.value.toString()),
            session.accessToken.token.toString(),
            ChannelRoleUpdateInputModel(role),
        ).handle { }
    }
}
