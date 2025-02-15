package pt.isel.pdm.chimp.domain.pagination

/**
 * Represents a pagination output.
 *
 * @param items the items on the current page
 * @param info the pagination info
 */
data class Pagination<T>(
    val items: List<T> = emptyList(),
    val info: PaginationInfo = PaginationInfo(),
)
