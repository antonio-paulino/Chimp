package pt.isel.pdm.chimp.dto.output.invitations

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

@Serializable
data class ChannelInvitationsPaginatedOutputModel(
    val invitations: List<ChannelInvitationOutputModel>,
    val pagination: PaginationOutputModel,
) {
    fun toDomain() =
        Pagination(
            items = invitations.map { it.toDomain() },
            info = pagination.toInfo(),
        )
}
