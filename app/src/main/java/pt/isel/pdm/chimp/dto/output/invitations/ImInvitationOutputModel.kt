package pt.isel.pdm.chimp.dto.output.invitations

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.invitations.ImInvitationStatus
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class ImInvitationOutputModel(
    val token: String,
    val status: String,
    val expiresAt: String,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain() =
        ImInvitation(
            token = UUID.fromString(token),
            status = ImInvitationStatus.valueOf(status),
            expiresAt = LocalDateTime.parse(expiresAt),
        )
}
