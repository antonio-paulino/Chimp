package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable

@Serializable
data class ChannelCreationOutputModel(
    val id: Long,
    val createdAt: String,
)
