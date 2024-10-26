package pt.isel.pdm.chimp.dto.output.credentials

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.tokens.AccessToken
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class AccessTokenOutputModel(
    val token: String,
    val expiresAt: String,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain() = AccessToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))
}