package pt.isel.pdm.chimp.dto.output.users

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel

@Serializable
data class UserChannelsOutputModel(
    val ownedChannels: List<ChannelOutputModel>,
    val memberChannels: List<ChannelOutputModel>,
) {
    fun toDomain() =
        Pair(
            ownedChannels.map { it.toDomain() },
            memberChannels.map { it.toDomain() },
        )
}
