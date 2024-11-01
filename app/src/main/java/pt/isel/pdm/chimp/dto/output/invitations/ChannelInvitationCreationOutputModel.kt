package pt.isel.pdm.chimp.dto.output.invitations

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import java.time.LocalDateTime

@Serializable
data class ChannelInvitationCreationOutputModel(
    val id: Long,
) {
    fun toDomain(
        channel: Channel,
        invitee: User,
        inviter: User,
        role: ChannelRole,
        expiresAt: LocalDateTime,
    ) = ChannelInvitation(
        id = id.toIdentifier(),
        channel = channel,
        invitee = invitee,
        inviter = inviter,
        role = role,
        expiresAt = expiresAt,
    )
}
