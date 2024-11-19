package pt.isel.pdm.chimp.domain.invitations

import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import java.time.LocalDateTime

/**
 * Represents an invitation to a channel.
 *
 * @property id The unique identifier of the invitation.
 * @property channel The channel to which the user is invited.
 * @property inviter The user that sent the invitation.
 * @property invitee The user that received the invitation.
 * @property status The status of the invitation.
 * @property role The role that the user will have in the channel.
 * @property expiresAt The date and time when the invitation expires.
 */
data class ChannelInvitation(
    override val id: Identifier = Identifier(0),
    val channel: Channel,
    val inviter: User,
    val invitee: User,
    val status: ChannelInvitationStatus = ChannelInvitationStatus.PENDING,
    val role: ChannelRole,
    val expiresAt: LocalDateTime,
) : Identifiable
