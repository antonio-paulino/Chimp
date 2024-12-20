package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

/**
 * Output model for a message, received from the server.
 *
 * @property id The identifier of the message.
 * @property channelId The identifier of the channel.
 * @property author The author of the message.
 * @property content The content of the message.
 * @property createdAt The creation date of the message.
 * @property editedAt The edition date of the message.
 */
@Serializable
data class MessageOutputModel(
    val id: Long,
    val channelId: Long,
    val author: UserOutputModel,
    val content: String,
    val createdAt: String,
    val editedAt: String?,
) {
    fun toDomain() =
        Message(
            id = id.toIdentifier(),
            channelId = channelId.toIdentifier(),
            user = author.toDomain(),
            content = content,
            createdAt = LocalDateTime.parse(createdAt),
            editedAt = editedAt?.let { LocalDateTime.parse(it) },
        )

    companion object {
        fun fromDomain(message: Message) =
            MessageOutputModel(
                id = message.id.value,
                channelId = message.channelId.value,
                author = UserOutputModel.fromDomain(message.user),
                content = message.content,
                createdAt = message.createdAt.toString(),
                editedAt = message.editedAt?.toString(),
            )
    }
}
