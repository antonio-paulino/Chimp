package pt.isel.pdm.chimp.infrastructure.session

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

interface ChannelRepository {
    /**
     * Gets a list of channels.
     *
     * @param pagination The pagination information.
     * @param sort The sort information.
     * @param after The identifier of the last channel currently in view.
     * @param filterOwned If the channels should be filtered by the ones owned by the user.
     */
    suspend fun getChannels(
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
        filterOwned: Boolean?,
    ): Either<Problem, Pagination<Channel>>

    /**
     * Updates or creates channels in the Firestore database.
     */
    suspend fun updateChannels(channels: List<Channel>): Either<Problem, Unit>
}
