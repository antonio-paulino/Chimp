package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import java.time.LocalDateTime

@Serializable
data class ChannelInvitationUpdateInputModel(
    val role: ChannelRole,
    val expiresAt: String,
) {
    constructor(
        role: String,
        expiresAt: LocalDateTime,
    ) : this(
        ChannelRole.valueOf(role),
        expiresAt.toString(),
    )
}
