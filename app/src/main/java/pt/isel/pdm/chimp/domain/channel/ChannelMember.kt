package pt.isel.pdm.chimp.domain.channel

import pt.isel.pdm.chimp.domain.user.User

data class ChannelMember(
    val user: User,
    val role: ChannelRole,
)
