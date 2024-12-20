package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName

/**
 * Output model for a channel member, received from the server.
 *
 * @property id The identifier of the member.
 * @property name The name of the member.
 * @property role The role of the member.
 *
 */
@Serializable
data class ChannelMemberOutputModel(
    val id: Long,
    val name: String,
    val role: String,
) {
    fun toDomain(): ChannelMember {
        return ChannelMember(
            id = id.toIdentifier(),
            name = name.toName(),
            role = ChannelRole.valueOf(role),
        )
    }

    companion object {
        fun fromDomain(member: ChannelMember): ChannelMemberOutputModel {
            return ChannelMemberOutputModel(
                id = member.id.value,
                name = member.name.value,
                role = member.role.name,
            )
        }
    }
}
