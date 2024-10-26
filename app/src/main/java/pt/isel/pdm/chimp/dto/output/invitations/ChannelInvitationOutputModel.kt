package pt.isel.pdm.chimp.dto.output.invitations

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel

@Serializable
data class ChannelInvitationOutputModel(
    val id: Long,
    val channel: ChannelOutputModel,
    val inviter: UserOutputModel,
    val invitee: UserOutputModel,
    val status: String,
    val role: String,
    val expiresAt: String,
)