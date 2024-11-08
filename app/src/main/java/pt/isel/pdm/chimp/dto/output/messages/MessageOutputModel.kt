package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

@Serializable
data class MessageOutputModel(
    val id: Long,
    val channel: ChannelOutputModel,
    val author: UserOutputModel,
    val content: String,
    val createdAt: String,
    val editedAt: String?,
) {
    fun toDomain() =
        Message(
            id = id.toIdentifier(),
            channel = channel.toDomain(),
            user = author.toDomain(),
            content = content,
            createdAt = LocalDateTime.parse(createdAt),
            editedAt = editedAt?.let { LocalDateTime.parse(it) },
        )
}
