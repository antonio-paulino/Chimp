package pt.isel.pdm.chimp.dto.output.users

import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel

data class UserChannelsOutputModel(
    val ownedChannels: List<ChannelOutputModel>,
    val memberChannels: List<ChannelOutputModel>,
)
