package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import java.time.LocalDateTime

/**
 * Input model for inviting a user to a channel.
 *
 * @property invitee The user to invite.
 * @property expiresAt The expiration date of the invitation.
 * @property role The role of the user in the channel.
 */
@Serializable
class ChannelInvitationCreationInputModel(
    val invitee: String,
    val expiresAt: String,
    val role: ChannelRole,
) {
    constructor(
        invitee: Identifier,
        expiresAt: LocalDateTime,
        role: ChannelRole,
    ) : this(
        invitee.value.toString(),
        expiresAt.toString(),
        role,
    )
}
