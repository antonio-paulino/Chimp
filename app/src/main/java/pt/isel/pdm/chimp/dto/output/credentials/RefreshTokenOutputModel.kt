package pt.isel.pdm.chimp.dto.output.credentials

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.tokens.RefreshToken
import java.time.LocalDateTime
import java.util.UUID

/**
 * Output model for a refresh token, received from the server.
 *
 * @property token The token.
 * @property expiresAt The expiration date of the token.
 */
@Serializable
data class RefreshTokenOutputModel(
    val token: String,
    val expiresAt: String,
) {
    fun toDomain() = RefreshToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))
}
