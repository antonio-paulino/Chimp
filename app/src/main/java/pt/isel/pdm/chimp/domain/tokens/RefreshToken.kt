package pt.isel.pdm.chimp.domain.tokens

import java.time.LocalDateTime
import java.util.UUID

/**
 * A refresh token that can be used to obtain a new access token.
 * @property token The unique identifier of the refresh token.
 */
data class RefreshToken(
    val token: UUID = UUID.randomUUID(),
    val expiresAt: LocalDateTime,
)
