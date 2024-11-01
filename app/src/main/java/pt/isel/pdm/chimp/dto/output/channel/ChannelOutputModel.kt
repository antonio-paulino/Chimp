package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

@Serializable
data class ChannelOutputModel(
    val id: Long,
    val name: String,
    val defaultRole: String,
    val owner: UserOutputModel,
    val isPublic: Boolean,
    val members: List<ChannelMemberOutputModel>,
    val createdAt: String,
) {
    fun toDomain(): Channel {
        return Channel(
            id = id.toIdentifier(),
            name = name.toName(),
            defaultRole = ChannelRole.valueOf(defaultRole),
            owner = owner.toDomain(),
            isPublic = isPublic,
            createdAt = LocalDateTime.parse(createdAt),
            members = members.map { it.toDomain() },
        )
    }
}
