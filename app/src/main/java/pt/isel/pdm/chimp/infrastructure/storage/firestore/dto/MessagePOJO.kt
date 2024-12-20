package pt.isel.pdm.chimp.infrastructure.storage.firestore.dto

import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import java.time.LocalDateTime
import java.time.ZoneOffset

data class MessagePOJO(
    var id: Long = 0,
    var channelId: Long = 0,
    var author: UserPOJO = UserPOJO(),
    var content: String = "",
    var createdAt: Long = 0,
    var editedAt: String? = null,
) {
    fun toDomain() =
        Message(
            id = id.toIdentifier(),
            channelId = channelId.toIdentifier(),
            author = author.toDomain(),
            content = content,
            createdAt = LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.UTC),
            editedAt = editedAt?.let { LocalDateTime.parse(it) },
        )

    companion object {
        fun fromDomain(message: Message) =
            MessagePOJO(
                id = message.id.value,
                channelId = message.channelId.value,
                author = UserPOJO.fromDomain(message.author),
                content = message.content,
                createdAt = message.createdAt.toEpochSecond(ZoneOffset.UTC),
                editedAt = message.editedAt?.toString(),
            )
    }
}
