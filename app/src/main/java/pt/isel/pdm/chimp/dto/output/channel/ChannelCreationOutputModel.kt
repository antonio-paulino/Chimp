package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name

/**
 * Output model for creating a channel, received from the server.
 *
 * @property id The identifier of the channel.
 * @property createdAt The creation date of the channel.
 *
 */
@Serializable
data class ChannelCreationOutputModel(
    val id: Long,
    val createdAt: String,
) {
    fun toDomain(
        name: Name,
        defaultRole: ChannelRole,
        isPublic: Boolean,
        owner: User,
    ) = Channel(
        id = id.toIdentifier(),
        name = name,
        defaultRole = defaultRole,
        isPublic = isPublic,
        owner = owner,
        members = listOf(ChannelMember(owner.id, owner.name, ChannelRole.OWNER)),
    )
}
