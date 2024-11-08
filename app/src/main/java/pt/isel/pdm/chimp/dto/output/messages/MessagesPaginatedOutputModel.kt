package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

@Serializable
data class MessagesPaginatedOutputModel(
    val messages: List<MessageOutputModel>,
    val pagination: PaginationOutputModel,
) {
    fun toDomain() =
        Pagination(
            items = messages.map { it.toDomain() },
            info = pagination.toInfo(),
        )
}
