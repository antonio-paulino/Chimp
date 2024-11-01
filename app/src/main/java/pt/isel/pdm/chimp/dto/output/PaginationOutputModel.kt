package pt.isel.pdm.chimp.dto.output

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.PaginationInfo

@Serializable
data class PaginationOutputModel(
    val total: Long?,
    val totalPages: Int?,
    val current: Int,
    val next: Int?,
    val previous: Int?,
) {
    fun toInfo(): PaginationInfo {
        return PaginationInfo(
            total = total,
            totalPages = totalPages,
            currentPage = current,
            nextPage = next,
            prevPage = previous,
        )
    }
}
