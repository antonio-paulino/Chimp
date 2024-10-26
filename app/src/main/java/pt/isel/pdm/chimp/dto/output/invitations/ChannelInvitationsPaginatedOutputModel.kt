package pt.isel.pdm.chimp.dto.output.invitations

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

@Serializable
data class ChannelInvitationsPaginatedOutputModel(
    val invitations: List<ChannelInvitationOutputModel>,
    val pagination: PaginationOutputModel? = null,
)
