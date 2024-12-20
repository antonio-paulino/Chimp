package pt.isel.pdm.chimp.infrastructure.services.http.users

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
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
    /**
     * The implementation of the [UserService.getUserById] method.
     *
     * @param userId The id of the user to get.
     * @param session The session of the user getting the user.
     *
     * @return An [Either] containing the [User] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun getUserById(
        userId: Long,
        session: Session,
    ): Either<Problem, User> =
        get<UserOutputModel>(
            USER_ROUTE.replace(USER_ID_PARAM, userId.toString()),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }

    /**
     * The implementation of the [UserService.getUsers] method.
     *
     * @param name The name of the users to get.
     * @param session The session of the user getting the users.
     * @param pagination The pagination information.
     * @param sort The sorting information.
     *
     * @return An [Either] containing the [Pagination] of [User] if the operation was successful or a [Problem] if it was not.
     */
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

    /**
     * The implementation of the [UserService.getUserChannels] method.
     *
     * @param session The session of the user getting the channels.
     * @param pagination The pagination information.
     * @param sort The sorting information.
     * @param filterOwned Whether to filter the channels owned by the user.
     * @param after The identifier to get the channels after.
     *
     * @return An [Either] containing the [Pagination] of [Channel] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun getUserChannels(
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        filterOwned: Boolean,
        after: Identifier?,
    ): Either<Problem, Pagination<Channel>> {
        return get<ChannelsPaginatedOutputModel>(
            USER_CHANNELS_ROUTE
                .replace(USER_ID_PARAM, session.user.id.value.toString())
                .plus(buildQuery(null, pagination, sort, filterOwned, after = after)),
            session.accessToken.token.toString(),
        ).handle { it.toDomain() }
    }
}
