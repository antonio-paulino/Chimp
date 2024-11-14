package pt.isel.pdm.chimp.infrastructure.services.interfaces.auth

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/*
 * Interface that defines the operations that can be performed on the authentication service
 */
interface AuthService {
    /**
     * Logs a user in with the given credentials
     *
     * @param username The username of the user
     * @param email The email of the user
     * @param password The password of the user
     *
     * @return Either a [Problem] or a [Session]
     */
    suspend fun login(
        username: String?,
        email: String?,
        password: String,
    ): Either<Problem, Session>

    /**
     * Registers a new user with the given credentials
     *
     * @param username The username of the user
     * @param email The email of the user
     * @param password The password of the user
     * @param token The invitation token for the user
     */
    suspend fun register(
        username: Name,
        email: Email,
        password: Password,
        token: ImInvitation,
    ): Either<Problem, Identifier>

    /**
     * Logs a user out
     *
     * @param session The session of the user to logout
     */
    suspend fun logout(session: Session): Either<Problem, Unit>

    /**
     * Refreshes the session
     *
     * @param session The session to refresh
     */
    suspend fun refresh(session: Session): Either<Problem, Session>

    /**
     * Creates an invitation for a new user
     *
     * @param session The session of the user creating the invitation
     */
    suspend fun createInvitation(session: Session): Either<Problem, ImInvitation>
}
