package pt.isel.pdm.chimp.infrastructure.services.http.users

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.dto.output.channel.ChannelsPaginatedOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import pt.isel.pdm.chimp.dto.output.users.UsersPaginatedOutputModel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.http.USERS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.USER_CHANNELS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.USER_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.USER_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.buildQuery
import pt.isel.pdm.chimp.infrastructure.services.http.handle
import pt.isel.pdm.chimp.infrastructure.services.interfaces.users.UserService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/**
 * HTTP implementation of the [UserService].
 */
class UserServiceHTTP(baseURL: String, httpClient: HttpClient) :
    BaseHTTPService(httpClient, baseURL), UserService {
    override suspend fun getUserById(
        userId: Long,
        session: Session,
    ): Either<Problem, User> =
        get<UserOutputModel>(
            USER_ROUTE.replace(USER_ID_PARAM, userId.toString()),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }

    override suspend fun getUsers(
        name: String?,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<User>> =
        get<UsersPaginatedOutputModel>(
            USERS_ROUTE + buildQuery(name, pagination, sort),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }

    override suspend fun getUserChannels(
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        filterOwned: Boolean,
    ): Either<Problem, Pagination<Channel>> {
        return get<ChannelsPaginatedOutputModel>(
            USER_CHANNELS_ROUTE
                .replace(USER_ID_PARAM, session.id.toString())
                .plus(buildQuery(null, pagination, sort, filterOwned)),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }
}
