package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName

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
}
