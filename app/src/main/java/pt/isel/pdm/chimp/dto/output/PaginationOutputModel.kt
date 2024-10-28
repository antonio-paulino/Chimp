package pt.isel.pdm.chimp.dto.output

import kotlinx.serialization.Serializable

@Serializable
data class PaginationOutputModel(
    val total: Long?,
    val totalPages: Int?,
    val current: Int,
    val next: Int?,
    val previous: Int?,
)
