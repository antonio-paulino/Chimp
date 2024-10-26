package pt.isel.pdm.chimp.dto.output.credentials

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel

@Serializable
data class CredentialsOutputModel(
    val sessionID: Long,
    val user: UserOutputModel,
    val accessToken: AccessTokenOutputModel,
    val refreshToken: RefreshTokenOutputModel,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): Session {
        val refreshToken = refreshToken.toDomain()
        return Session(
            sessionID.toIdentifier(),
            user.toDomain(),
            accessToken.toDomain(),
            refreshToken,
            refreshToken.expiresAt,
        )
    }
}
