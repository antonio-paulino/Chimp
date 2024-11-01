package pt.isel.pdm.chimp.dto.output.credentials

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.tokens.RefreshToken
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class RefreshTokenOutputModel(
    val token: String,
    val expiresAt: String,
) {
    fun toDomain() = RefreshToken(UUID.fromString(token), LocalDateTime.parse(expiresAt))
}
