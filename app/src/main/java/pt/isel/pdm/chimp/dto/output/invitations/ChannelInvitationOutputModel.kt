package pt.isel.pdm.chimp.dto.output.invitations

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitationStatus
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel
import java.time.LocalDateTime

/**
 * Output model for a channel invitation, received from the server.
 *
 * @property id The identifier of the invitation.
 * @property channel The channel.
 * @property inviter The inviter.
 * @property invitee The invitee.
 * @property status The status of the invitation.
 * @property role The role of the invitee.
 * @property expiresAt The expiration date of the invitation.
 */
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
