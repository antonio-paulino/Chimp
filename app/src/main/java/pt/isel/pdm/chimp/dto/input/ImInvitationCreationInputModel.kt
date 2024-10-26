package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Input model for creating an invitation to the application.
 *
 * @property expiresAt The expiration date of the invitation.
 */
@Serializable
data class ImInvitationCreationInputModel(
    val expiresAt: String,
) {
    constructor(
        expiresAt: LocalDateTime,
    ) : this(
        expiresAt.toString(),
    )
}
