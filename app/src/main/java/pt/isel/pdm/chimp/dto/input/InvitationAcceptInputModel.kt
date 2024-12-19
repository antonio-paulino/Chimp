package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitationStatus

/**
 * Input model for accepting or rejecting an invitation.
 *
 * @property status The status of the invitation.
 */
@Serializable
data class InvitationAcceptInputModel(
    val status: String,
) {
    constructor(
        accept: Boolean,
    ) : this(
        when (accept) {
            true -> ChannelInvitationStatus.ACCEPTED.name
            false -> ChannelInvitationStatus.REJECTED.name
        }
    )
}
