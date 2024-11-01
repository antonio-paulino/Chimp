package pt.isel.pdm.chimp.dto.output.credentials

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.tokens.AccessToken
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class AccessTokenOutputModel(
    val token: String,
    val expiresAt: String,
) {
    fun toDomain() = AccessToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))
}
