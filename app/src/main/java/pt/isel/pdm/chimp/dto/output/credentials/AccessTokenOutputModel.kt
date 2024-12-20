package pt.isel.pdm.chimp.dto.output.credentials

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.tokens.AccessToken
import java.time.LocalDateTime
import java.util.UUID

/**
 * Output model for an access token, received from the server.
 *
 * @property token The token.
 * @property expiresAt The expiration date of the token.
 */
@Serializable
data class AccessTokenOutputModel(
    val token: String,
    val expiresAt: String,
) {
    fun toDomain() = AccessToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))
}
