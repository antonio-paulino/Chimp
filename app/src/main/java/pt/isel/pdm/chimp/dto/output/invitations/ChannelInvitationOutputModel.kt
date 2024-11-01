package pt.isel.pdm.chimp.dto.output.invitations

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitationStatus
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

@Serializable
data class ChannelInvitationOutputModel(
    val id: Long,
    val channel: ChannelOutputModel,
    val inviter: UserOutputModel,
    val invitee: UserOutputModel,
    val status: String,
    val role: String,
    val expiresAt: String,
) {
    fun toDomain() =
        ChannelInvitation(
            id = id.toIdentifier(),
            channel = channel.toDomain(),
            inviter = inviter.toDomain(),
            invitee = invitee.toDomain(),
            status = ChannelInvitationStatus.valueOf(status),
            role = ChannelRole.valueOf(role),
            expiresAt = LocalDateTime.parse(expiresAt),
        )
}
