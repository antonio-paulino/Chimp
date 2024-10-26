package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable

@Serializable
data class MessageCreationOutputModel(
    val id: Long,
    val createdAt: String,
    val editedAt: String?,
)