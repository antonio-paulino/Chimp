package pt.isel.pdm.chimp.domain.pagination

/**
 * Represents pagination information.
 *
 * @param total the total number of items matching the query criteria
 * @param currentPage the current page number
 * @param totalPages the total number of pages available
 * @param nextPage the next page number or null if there is no next page
 * @param prevPage the previous page number or null if there is no previous page
 */
data class PaginationInfo(
    val total: Long? = null,
    val currentPage: Int = 1,
    val totalPages: Int? = null,
    val nextPage: Int? = null,
    val prevPage: Int? = null,
)
