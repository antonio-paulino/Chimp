package pt.isel.pdm.chimp.dto.output.users

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

@Serializable
data class UsersPaginatedOutputModel(
    val users: List<UserOutputModel>,
    val pagination: PaginationOutputModel?,
) {
    fun toDomain() =
        Pagination(
            items = users.map { it.toDomain() },
            info = pagination?.toInfo(),
        )
}
