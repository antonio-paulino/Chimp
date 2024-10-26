package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable

@Serializable
data class MessageUpdateOutputModel(
    val editedAt: String,
)
