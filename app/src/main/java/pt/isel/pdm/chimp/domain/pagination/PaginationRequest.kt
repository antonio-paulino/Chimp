package pt.isel.pdm.chimp.domain.pagination

/**
 * Represents a pagination request.
 *
 * Page indexing starts at 1 when using this class.
 *
 * @param offset the index of the first element to return
 * @param limit the maximum number of elements to return
 * @param getCount whether to count the total number of elements
 */
data class PaginationRequest(
    val offset: Long,
    val limit: Long,
    val getCount: Boolean = false,
)
