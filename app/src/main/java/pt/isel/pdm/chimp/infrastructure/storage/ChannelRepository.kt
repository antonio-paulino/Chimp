package pt.isel.pdm.chimp.infrastructure.storage

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

interface ChannelRepository {
    /**
     * Gets a list of channels.
     *
     * @param limit The maximum number of channels to return.
     * @param getCount Whether to count the total number of channels.
     * @param sortDirection The direction of the sort.
     * @param after The identifier of the last channel currently in view.
     * @param filterOwned If the channels should be filtered by the ones owned by the user.
     */
    suspend fun getChannels(
        limit: Long = 10,
        getCount: Boolean = false,
        sortDirection: Sort = Sort.ASC,
        after: Identifier?,
        filterOwned: Boolean?,
    ): Either<Problem, Pagination<Channel>>

    /**
     * Updates or creates channels in the database.
     */
    suspend fun updateChannels(channels: List<Channel>): Either<Problem, Unit>

    /**
     * Deletes a channel from the database.
     */
    suspend fun deleteChannel(channelIdentifier: Identifier): Either<Problem, Unit>
}
