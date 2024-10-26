package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel

@Serializable
data class MessageOutputModel(
    val id: Long,
    val author: UserOutputModel,
    val content: String,
    val createdAt: String,
    val editedAt: String?,
)