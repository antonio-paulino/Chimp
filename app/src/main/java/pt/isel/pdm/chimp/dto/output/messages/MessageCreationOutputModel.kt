package pt.isel.pdm.chimp.dto.output.messages

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import java.time.LocalDateTime

@Serializable
data class MessageCreationOutputModel(
    val id: Long,
    val createdAt: String,
    val editedAt: String?,
) {
    fun toDomain(
        channel: Channel,
        user: User,
        content: String,
    ) = Message(
        id = id.toIdentifier(),
        channelId = channel.id,
        user = user,
        content = content,
        createdAt = LocalDateTime.parse(createdAt),
        editedAt = editedAt?.let { LocalDateTime.parse(it) },
    )
}
