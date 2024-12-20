package pt.isel.pdm.chimp.infrastructure.services.http.auth

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password
import pt.isel.pdm.chimp.dto.input.AuthenticationInputModel
import pt.isel.pdm.chimp.dto.input.ImInvitationCreationInputModel
import pt.isel.pdm.chimp.dto.input.UserCreationInputModel
import pt.isel.pdm.chimp.dto.output.credentials.CredentialsOutputModel
import pt.isel.pdm.chimp.dto.output.invitations.ImInvitationOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserCreationOutputModel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.http.INVITATIONS_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.LOGIN_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.LOGOUT_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.REFRESH_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.REGISTER_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.handle
import pt.isel.pdm.chimp.infrastructure.services.interfaces.auth.AuthService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import java.time.LocalDateTime

/**
 * Implementation of the [AuthService] interface that uses HTTP to communicate with the backend.
 *
 * @property baseUrl The base URL of the server.
 * @property httpClient The HTTP client used to communicate with the server.
 */
class AuthServiceHTTP(
    baseUrl: String,
    httpClient: HttpClient,
) : BaseHTTPService(httpClient, baseUrl), AuthService {
    /**
     * The implementation of the [AuthService.login] method.
     *
     * @param username The username of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     *
     * @return An [Either] containing the [Session] if the login was successful or a [Problem] if it was not.
     */
    override suspend fun login(
        username: String?,
        email: String?,
        password: String,
    ): Either<Problem, Session> =
        post<AuthenticationInputModel, CredentialsOutputModel>(
            LOGIN_ROUTE,
            "",
            AuthenticationInputModel(username, password, email),
        ).handle { it.toDomain() }

    /**
     * The implementation of the [AuthService.register] method.
     *
     * @param username The username of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param token The invitation token.
     *
     * @return An [Either] containing the [Identifier] of the user if the registration was successful or a [Problem] if it was not.
     */
    override suspend fun register(
        username: Name,
        email: Email,
        password: Password,
        token: ImInvitation,
    ): Either<Problem, Identifier> =
        post<UserCreationInputModel, UserCreationOutputModel>(
            REGISTER_ROUTE,
            "",
            UserCreationInputModel(username, password, email, token),
        ).handle { it.id.toIdentifier() }

    /**
     * The implementation of the [AuthService.logout] method.
     *
     * @param session The session to be logged out.
     *
     * @return An [Either] containing [Unit] if the logout was successful or a [Problem] if it was not.
     */
    override suspend fun logout(session: Session): Either<Problem, Unit> =
        post<Unit, Unit>(LOGOUT_ROUTE, session.accessToken.token.toString(), null).handle { }

    /**
     * The implementation of the [AuthService.refresh] method.
     */
    override suspend fun refresh(session: Session): Either<Problem, Session> =
        post<Unit, CredentialsOutputModel>(
            REFRESH_ROUTE,
            session.refreshToken.token.toString(),
            null,
        ).handle { it.toDomain() }

    /**
     * The implementation of the [AuthService.createInvitation] method.
     *
     * @param session The session of the user creating the invitation.
     * @param expirationDate The expiration date of the invitation.
     *
     * @return An [Either] containing the [ImInvitation] if the creation was successful or a [Problem] if it was not.
     */
    override suspend fun createInvitation(
        session: Session,
        expirationDate: LocalDateTime,
    ): Either<Problem, ImInvitation> =
        post<ImInvitationCreationInputModel, ImInvitationOutputModel>(
            INVITATIONS_ROUTE,
            session.accessToken.token.toString(),
            ImInvitationCreationInputModel(expirationDate),
        ).handle { it.toDomain() }
}
