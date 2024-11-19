package pt.isel.pdm.chimp.infrastructure.services.interfaces.users

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/**
 * Service that provides operations related to users.
 */
interface UserService {
    /**
     * Gets the user with the given identifier.
     *
     * @param userId The identifier of the user.
     * @param session The session of the user.
     *
     * @return Either a [Problem] or the user with the given identifier.
     */
    suspend fun getUserById(
        userId: Long,
        session: Session,
    ): Either<Problem, User>

    /**
     * Gets all the users.
     *
     * @param pagination The pagination information.
     * @param sort The sort information.
     * @param session The session of the user.
     * @param name The partial name of the user.
     *
     * @return Either a [Problem] or a list with all the users.
     */
    suspend fun getUsers(
        name: String?,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<User>>

    /**
     * Gets the channels of the user.
     *
     * @param sort The sort information.
     * @param pagination The pagination information.
     * @param filterOwned Whether to filter by owned channels.
     * @param session The session of the user.
     *
     * @return Either a [Problem] or a list with all the channels of the user.
     */
    suspend fun getUserChannels(
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        filterOwned: Boolean,
    ): Either<Problem, Pagination<Channel>>
}
