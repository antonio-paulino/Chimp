package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable

@Serializable
data class ChannelMemberOutputModel(
    val id: Long,
    val name: String,
    val role: String,
)