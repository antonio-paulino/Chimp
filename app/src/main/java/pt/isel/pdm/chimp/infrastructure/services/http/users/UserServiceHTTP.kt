package pt.isel.pdm.chimp.infrastructure.services.http.users

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.UserChannels
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.dto.output.users.UserChannelsOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import pt.isel.pdm.chimp.dto.output.users.UsersPaginatedOutputModel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
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
        ).handle { it!!.toDomain() }

    override suspend fun getUsers(
        name: String?,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<User>> =
        get<UsersPaginatedOutputModel>(
            USERS_ROUTE + buildQuery(name, pagination, sort),
            session.accessToken.token.toString(),
        ).handle { it!!.toDomain() }

    override suspend fun getUserChannels(
        user: User,
        session: Session,
        sort: SortRequest?,
    ): Either<Problem, UserChannels> {
        return get<UserChannelsOutputModel>(
            USER_CHANNELS_ROUTE
                .replace(USER_ID_PARAM, user.id.toString())
                .plus(buildQuery(null, null, sort)),
            session.accessToken.token.toString(),
        ).handle { it!!.toDomain() }
    }

    companion object {
        private const val USER_ID_PARAM = "{userId}"
        private const val USERS_ROUTE = "users"
        private const val USER_ROUTE = "users/$USER_ID_PARAM"
        private const val USER_CHANNELS_ROUTE = "users/$USER_ID_PARAM/channels"
    }
}
