package pt.isel.pdm.chimp.dto.output.users

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

/**
 * Output model for a paginated list of users, received from the server.
 *
 * @property users The list of users.
 * @property pagination The pagination information.
 */
@Serializable
data class UsersPaginatedOutputModel(
    val users: List<UserOutputModel>,
    val pagination: PaginationOutputModel,
) {
    fun toDomain() =
        Pagination(
            items = users.map { it.toDomain() },
            info = pagination.toInfo(),
        )
}
