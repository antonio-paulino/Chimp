package pt.isel.pdm.chimp.infrastructure.storage.firestore.dto

import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName

data class ChannelMemberPOJO(
    var id: Long = 0,
    var name: String = "",
    var role: String = "",
) {
    fun toDomain() = ChannelMember(id.toIdentifier(), name.toName(), ChannelRole.valueOf(role))

    companion object {
        fun fromDomain(channelMember: ChannelMember) =
            ChannelMemberPOJO(channelMember.id.value, channelMember.name.value, channelMember.role.name)
    }
}
