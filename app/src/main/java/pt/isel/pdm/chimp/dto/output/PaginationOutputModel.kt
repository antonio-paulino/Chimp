package pt.isel.pdm.chimp.dto.output

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.PaginationInfo

/**
 * Output model for pagination information, received from the server.
 *
 * @property total The total number of items.
 * @property totalPages The total number of pages.
 * @property current The current page.
 * @property next The next page.
 * @property previous The previous page.
 */
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
