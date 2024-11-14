package pt.isel.pdm.chimp.domain.pagination

/**
 * Represents a sort request.
 *
 * @property sortBy The field to sort by.
 * @property direction The direction of the sort.
 */
data class SortRequest(
    val sortBy: String?,
    val direction: Sort = Sort.ASC,
)
