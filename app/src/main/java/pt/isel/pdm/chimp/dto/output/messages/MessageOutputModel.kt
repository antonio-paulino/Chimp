package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

@Serializable
data class MessageOutputModel(
    val id: Long,
    val author: UserOutputModel,
    val content: String,
    val createdAt: String,
    val editedAt: String?,
) {
    fun toDomain(channel: Channel) =
        Message(
            id = id.toIdentifier(),
            channel = channel,
            user = author.toDomain(),
            content = content,
            createdAt = LocalDateTime.parse(createdAt),
            editedAt = editedAt?.let { LocalDateTime.parse(it) },
        )
}
