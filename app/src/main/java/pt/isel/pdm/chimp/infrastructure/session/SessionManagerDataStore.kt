package pt.isel.pdm.chimp.infrastructure.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.tokens.AccessToken
import pt.isel.pdm.chimp.domain.tokens.RefreshToken
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.email.toEmail
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName
import java.time.LocalDateTime
import java.util.UUID

class SessionManagerPreferencesDataStore(
    private val store: DataStore<Preferences>,
) : SessionManager {
    private val sessionKey = stringPreferencesKey(SESSION_KEY)

    override val session: Flow<Session?> =
        store.data.map { preferences ->
            preferences[sessionKey]?.let { Json.decodeFromString(PreferencesSession.serializer(), it).toSession() }
        }

    override suspend fun set(session: Session) {
        store.edit { preferences ->
            preferences[sessionKey] = Json.encodeToString(PreferencesSession.serializer(), PreferencesSession.fromSession(session))
        }
    }

    override suspend fun clear() {
        store.edit { preferences ->
            preferences.remove(sessionKey)
        }
    }

    companion object {
        private const val SESSION_KEY = "session"
    }
}

@Serializable
data class PreferencesSession(
    val id: Long,
    val user: PreferencesUser,
    val accessToken: PreferencesAccessToken,
    val refreshToken: PreferencesRefreshToken,
    val expiresAt: String,
) {
    fun toSession() =
        Session(
            id = id.toIdentifier(),
            user = user.toUser(),
            accessToken = accessToken.toAccessToken(),
            refreshToken = refreshToken.toRefreshToken(),
            expiresAt = LocalDateTime.parse(expiresAt),
        )

    companion object {
        fun fromSession(session: Session) =
            PreferencesSession(
                id = session.id.value,
                user = PreferencesUser.fromUser(session.user),
                accessToken = PreferencesAccessToken.fromAccessToken(session.accessToken),
                refreshToken = PreferencesRefreshToken.fromRefreshToken(session.refreshToken),
                expiresAt = session.expiresAt.toString(),
            )
    }
}

@Serializable
data class PreferencesUser(
    val id: Long,
    val username: String,
    val email: String,
) {
    fun toUser() = User(id.toIdentifier(), username.toName(), email.toEmail())

    companion object {
        fun fromUser(user: User) = PreferencesUser(user.id.value, user.name.value, user.email.value)
    }
}

@Serializable
data class PreferencesAccessToken(
    val token: String,
    val expiresAt: String,
) {
    fun toAccessToken() = AccessToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))

    companion object {
        fun fromAccessToken(accessToken: AccessToken) =
            PreferencesAccessToken(accessToken.token.toString(), accessToken.expiresAt.toString())
    }
}

@Serializable
data class PreferencesRefreshToken(
    val token: String,
    val expiresAt: String,
) {
    fun toRefreshToken() = RefreshToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))

    companion object {
        fun fromRefreshToken(refreshToken: RefreshToken) =
            PreferencesRefreshToken(refreshToken.token.toString(), refreshToken.expiresAt.toString())
    }
}
