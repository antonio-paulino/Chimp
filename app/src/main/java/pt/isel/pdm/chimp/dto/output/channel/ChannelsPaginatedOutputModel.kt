package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

@Serializable
data class ChannelsPaginatedOutputModel(
    val channels: List<ChannelOutputModel>,
    val pagination: PaginationOutputModel,
) {
    fun toDomain() =
        Pagination(
            items = channels.map { it.toDomain() },
            info = pagination.toInfo(),
        )
}
