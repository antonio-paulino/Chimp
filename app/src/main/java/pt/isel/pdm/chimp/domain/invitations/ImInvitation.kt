package pt.isel.pdm.chimp.domain.invitations

import java.time.LocalDateTime
import java.util.UUID

data class ImInvitation(
    val token: UUID,
    val expiresAt: LocalDateTime,
    val status: ImInvitationStatus = ImInvitationStatus.PENDING,
)