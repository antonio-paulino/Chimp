package pt.isel.pdm.chimp.domain.tokens

import java.time.LocalDateTime
import java.util.UUID

/**
 * An access token that can be used to authenticate requests.
 *
 * @property token The unique identifier of the access token.
 * @property expiresAt The date and time when the access token expires.
 */
data class AccessToken(
    val token: UUID = UUID.randomUUID(),
    val expiresAt: LocalDateTime,
) {
    fun toDomain() = AccessToken(token, expiresAt)
}
