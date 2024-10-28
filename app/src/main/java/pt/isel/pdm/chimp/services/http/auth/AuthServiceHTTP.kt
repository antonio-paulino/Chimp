package pt.isel.pdm.chimp.services.http.auth

import android.os.Build
import androidx.annotation.RequiresApi
import im.domain.wrappers.email.Email
import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.tokens.AccessToken
import pt.isel.pdm.chimp.domain.tokens.RefreshToken
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password
import pt.isel.pdm.chimp.dto.input.AuthenticationInputModel
import pt.isel.pdm.chimp.dto.output.credentials.CredentialsOutputModel
import pt.isel.pdm.chimp.dto.output.invitations.ImInvitationOutputModel
import pt.isel.pdm.chimp.dto.output.users.UserCreationOutputModel
import pt.isel.pdm.chimp.services.http.APIService
import pt.isel.pdm.chimp.services.http.handle
import pt.isel.pdm.chimp.services.interfaces.auth.AuthService
import pt.isel.pdm.chimp.services.media.problems.Problem
import java.util.UUID

/**
 * Implementation of the [AuthService] interface that uses HTTP to communicate with the backend.
 *
 * @property baseUrl The base URL of the server.
 * @property httpClient The HTTP client used to communicate with the server.
 */
class AuthServiceHTTP(
    baseUrl: String,
    httpClient: HttpClient,
) : APIService(httpClient, baseUrl), AuthService {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun login(
        username: Name?,
        email: Email?,
        password: Password,
    ): Either<Problem, Session> =
        post<AuthenticationInputModel, CredentialsOutputModel>(
            LOGIN_ROUTE,
            "",
            AuthenticationInputModel(username, password, email),
        ).handle { it!!.toDomain() }

    override suspend fun register(
        username: Name,
        email: Email,
        password: Password,
        token: UUID,
    ): Either<Problem, Identifier> =
        post<AuthenticationInputModel, UserCreationOutputModel>(
            REGISTER_ROUTE,
            "",
            AuthenticationInputModel(username, password, email),
        ).handle { it!!.id.toIdentifier() }

    override suspend fun logout(accessToken: AccessToken): Either<Problem, Unit> =
        post<Unit, Unit>(LOGOUT_ROUTE, accessToken.token.toString(), null).handle { }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun refresh(refreshToken: RefreshToken): Either<Problem, Session> =
        post<Unit, CredentialsOutputModel>(
            REFRESH_ROUTE,
            refreshToken.token.toString(),
            null,
        ).handle { it!!.toDomain() }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createInvitation(accessToken: AccessToken): Either<Problem, ImInvitation> =
        post<Unit, ImInvitationOutputModel>(
            INVITATIONS_ROUTE,
            accessToken.token.toString(),
            null,
        ).handle { it!!.toDomain() }

    companion object {
        private const val LOGIN_ROUTE = "api/auth/login"
        private const val REGISTER_ROUTE = "api/auth/register"
        private const val LOGOUT_ROUTE = "api/auth/logout"
        private const val REFRESH_ROUTE = "api/auth/refresh"
        private const val INVITATIONS_ROUTE = "api/auth/invitations"
    }
}
