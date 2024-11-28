package pt.isel.pdm.chimp.infrastructure.storage.firestore

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.ChannelRepository

class FireStoreChannelRepository : ChannelRepository {
    private val db = Firebase.firestore
    private val channelCollection = db.collection("channels")

    override suspend fun getChannels(
        pagination: PaginationRequest?,
        sort: SortRequest?,
        after: Identifier?,
        filterOwned: Boolean?,
    ): Either<Problem, Pagination<Channel>> {
        return try {
            val pageRequest =
                pagination?.let {
                    PaginationRequest(it.limit, it.offset, it.getCount)
                } ?: PaginationRequest(limit = 10, offset = 0, getCount = false)
            val sortRequest = sort?.let { SortRequest(it.sortBy, it.direction) } ?: SortRequest("id", Sort.ASC)

            val querySnapshot =
                channelCollection
                    .apply {
                        if (sortRequest.sortBy != null) {
                            orderBy(sortRequest.sortBy, sortRequest.direction.toFirestoreSort())
                        } else {
                            orderBy("id", sortRequest.direction.toFirestoreSort())
                        }
                        if (after != null) {
                            whereGreaterThan("id", after.value)
                        }
                        limit(pageRequest.limit)
                        startAt(pageRequest.offset)
                    }
                    .get().await()

            return success(querySnapshot.getPagination(ChannelOutputModel::class.java, ChannelOutputModel::toDomain, pageRequest))
        } catch (e: Exception) {
            Log.d("FirestoreMessageRepository", "Failed to get channel messages", e)
            failure(Problem.UnexpectedProblem)
        }
    }

    override suspend fun updateChannels(channels: List<Channel>): Either<Problem, Unit> {
        return try {
            val pendingSets =
                channels.map { channel ->
                    val channelOutputModel = ChannelOutputModel.fromDomain(channel)
                    channelCollection.document(channel.id.value.toString())
                        .set(channelOutputModel)
                }
            pendingSets.forEach { it.await() }
            success(Unit)
        } catch (e: Exception) {
            Log.d("FirestoreChannelRepository", "Failed to update channels", e)
            failure(Problem.UnexpectedProblem)
        }
    }
}
