package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable

@Serializable
data class ChannelsOutputModel(
    val channels: List<ChannelOutputModel>,
)