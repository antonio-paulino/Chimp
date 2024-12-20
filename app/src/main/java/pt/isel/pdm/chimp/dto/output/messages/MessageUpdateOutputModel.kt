package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import java.time.LocalDateTime

/**
 * Output model for an updated message, received from the server.
 *
 * @property editedAt The edition date of the message.
 */
@Serializable
data class MessageUpdateOutputModel(
    val editedAt: String,
) {
    fun toDomain(
        id: Identifier,
        channel: Channel,
        content: String,
        author: User,
        createdAt: LocalDateTime,
    ) = Message(
        id = id,
        channelId = channel.id,
        content = content,
        user = author,
        createdAt = createdAt,
        editedAt = LocalDateTime.parse(editedAt),
    )
}
