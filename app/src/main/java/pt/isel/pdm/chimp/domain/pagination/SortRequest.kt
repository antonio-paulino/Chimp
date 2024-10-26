package pt.isel.pdm.chimp.domain.pagination

data class SortRequest(
    val sortBy: String?,
    val direction: Sort = Sort.ASC,
)
