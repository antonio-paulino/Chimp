package pt.isel.pdm.chimp.services.interfaces.auth

import im.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.tokens.AccessToken
import pt.isel.pdm.chimp.domain.tokens.RefreshToken
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password
import pt.isel.pdm.chimp.services.media.problems.Problem
import java.util.UUID

/*
 * Interface that defines the operations that can be performed on the authentication service
 */
interface AuthService {
    /**
     * Logs a user in with the given credentials
     */
    suspend fun login(
        username: Name?,
        email: Email?,
        password: Password,
    ): Either<Problem, Session>

    /**
     * Registers a new user with the given credentials
     */
    suspend fun register(
        username: Name,
        email: Email,
        password: Password,
        token: UUID,
    ): Either<Problem, Identifier>

    /**
     * Logs a user out
     */
    suspend fun logout(accessToken: AccessToken): Either<Problem, Unit>

    /**
     * Refreshes the session
     */
    suspend fun refresh(refreshToken: RefreshToken): Either<Problem, Session>

    /**
     * Creates an invitation for a new user
     */
    suspend fun createInvitation(accessToken: AccessToken): Either<Problem, ImInvitation>
}
