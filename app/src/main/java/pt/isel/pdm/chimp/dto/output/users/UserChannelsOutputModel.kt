package pt.isel.pdm.chimp.dto.output.users

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel

/**
 * Output model for the list of channels owned and member of a user, received from the server.
 *
 * @property ownedChannels The list of channels owned by the user.
 * @property memberChannels The list of channels the user is a member of.
 */
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
