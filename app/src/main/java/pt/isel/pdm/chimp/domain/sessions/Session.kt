package pt.isel.pdm.chimp.domain.sessions

import pt.isel.pdm.chimp.domain.tokens.AccessToken
import pt.isel.pdm.chimp.domain.tokens.RefreshToken
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import java.time.LocalDateTime

/**
 * Represents a session for a user.
 *
 * @property id The unique identifier of the session.
 * @property user The user that owns the session.
 * @property expiresAt The date and time when the session expires.
 */
data class Session(
    val id: Identifier = Identifier(0),
    val user: User,
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val expiresAt: LocalDateTime,
) {
    val expired: Boolean
        get() = LocalDateTime.now() >= expiresAt
}
