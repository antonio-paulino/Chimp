package pt.isel.pdm.chimp.domain.channel

import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name

data class ChannelMember(
    val id: Identifier,
    val name: Name,
    val role: ChannelRole,
)
