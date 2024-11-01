package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable

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
        accept.toString(),
    )
}
