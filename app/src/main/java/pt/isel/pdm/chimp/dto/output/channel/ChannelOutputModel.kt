package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

/**
 * Output model for a channel, received from the server.
 *
 * @property id The identifier of the channel.
 * @property name The name of the channel.
 * @property defaultRole The default role of the channel.
 * @property owner The owner of the channel.
 */
@Serializable
data class ChannelOutputModel(
    val id: Long,
    val name: String,
    val defaultRole: String,
    val owner: UserOutputModel,
    val isPublic: Boolean,
    val members: List<ChannelMemberOutputModel>,
    val createdAt: String = System.currentTimeMillis().toString(),
) {
    fun toDomain(): Channel {
        return Channel(
            id = id.toIdentifier(),
            name = name.toName(),
            defaultRole = ChannelRole.valueOf(defaultRole),
            owner = owner.toDomain(),
            isPublic = isPublic,
            members = members.map { it.toDomain() },
            createdAt = LocalDateTime.parse(createdAt),
        )
    }

    companion object {
        fun fromDomain(channel: Channel): ChannelOutputModel {
            return ChannelOutputModel(
                id = channel.id.value,
                name = channel.name.value,
                defaultRole = channel.defaultRole.name,
                owner = UserOutputModel.fromDomain(channel.owner),
                isPublic = channel.isPublic,
                members = channel.members.map { ChannelMemberOutputModel.fromDomain(it) },
                createdAt = channel.createdAt.toString(),
            )
        }
    }
}
