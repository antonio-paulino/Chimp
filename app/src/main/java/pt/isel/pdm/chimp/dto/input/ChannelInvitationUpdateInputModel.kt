package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import java.time.LocalDateTime

@Serializable
data class ChannelInvitationUpdateInputModel(
    val role: String?,
    val expiresAt: String?,
) {
    constructor(
        role: ChannelRole?,
        expiresAt: LocalDateTime?,
    ) : this(
        role = role?.name,
        expiresAt = expiresAt?.toString(),
    )
}
