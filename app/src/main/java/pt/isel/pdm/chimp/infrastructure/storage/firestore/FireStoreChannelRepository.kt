package pt.isel.pdm.chimp.infrastructure.storage.firestore

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.storage.ChannelRepository
import pt.isel.pdm.chimp.infrastructure.storage.firestore.dto.ChannelPOJO

class FireStoreChannelRepository : ChannelRepository {
    private val db = Firebase.firestore
    private val channelCollection = db.collection("channels")

    override suspend fun getChannels(
        limit: Long,
        getCount: Boolean,
        sortDirection: Sort,
        after: Identifier?,
        filterOwned: Boolean?,
    ): Either<Problem, Pagination<Channel>> {
        return try {
            val querySnapshot =
                channelCollection
                    .orderBy("id", sortDirection.toFirestoreSort())
                    .whereGreaterThan("id", after?.value ?: 0)
                    .limit(limit)
                    .get().await()

            return success(querySnapshot.getPagination(ChannelPOJO::class.java, ChannelPOJO::toDomain, limit, getCount))
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get channel messages", e)
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
            Log.d(TAG, "Failed to update channels", e)
            failure(Problem.UnexpectedProblem)
        }
    }

    override suspend fun deleteChannel(channelIdentifier: Identifier): Either<Problem, Unit> {
        return try {
            channelCollection.document(channelIdentifier.value.toString())
                .delete()
                .await()
            success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete channel", e)
            failure(Problem.UnexpectedProblem)
        }
    }
}
