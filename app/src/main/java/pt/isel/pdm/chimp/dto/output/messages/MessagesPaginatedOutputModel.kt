package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

@Serializable
data class MessagesPaginatedOutputModel(
    val messages: List<MessageOutputModel>,
    val pagination: PaginationOutputModel,
) {
    fun toDomain(channel: Channel) =
        Pagination(
            items = messages.map { it.toDomain(channel) },
            info = pagination.toInfo(),
        )
}
