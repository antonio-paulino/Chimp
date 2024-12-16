package pt.isel.pdm.chimp.infrastructure.storage.firestore.dto

import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import java.time.LocalDateTime

data class MessagePOJO(
    var id: Long = 0,
    var channelId: Long = 0,
    var author: UserPOJO = UserPOJO(),
    var content: String = "",
    var createdAt: String = "",
    var editedAt: String? = null
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
}