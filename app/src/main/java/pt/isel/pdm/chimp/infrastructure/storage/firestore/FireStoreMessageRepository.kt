package pt.isel.pdm.chimp.infrastructure.storage.firestore

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.dto.output.messages.MessageOutputModel
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.storage.MessageRepository
import java.time.LocalDateTime

class FireStoreMessageRepository : MessageRepository {
    private val db = Firebase.firestore
    private val messageCollection = db.collection("messages")

    override suspend fun getChannelMessages(
        channel: Channel,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        before: LocalDateTime?,
    ): Either<Problem, Pagination<Message>> {
        return try {
            val pageRequest =
                pagination?.let {
                    PaginationRequest(it.limit, it.offset, it.getCount)
                } ?: PaginationRequest(limit = 10, offset = 0, getCount = false)
            val sortRequest = sort?.let { SortRequest(it.sortBy, it.direction) } ?: SortRequest("createdAt", Sort.DESC)

            val querySnapshot =
                messageCollection
                    .whereEqualTo("channelId", channel.id.value)
                    .apply {
                        if (sortRequest.sortBy != null) {
                            orderBy(sortRequest.sortBy, sortRequest.direction.toFirestoreSort())
                        } else {
                            orderBy("createdAt", sortRequest.direction.toFirestoreSort())
                        }
                        if (before != null) {
                            whereLessThan("createdAt", before)
                        }
                        limit(pageRequest.limit)
                        startAt(pageRequest.offset)
                    }
                    .get().await()

            return success(querySnapshot.getPagination(MessageOutputModel::class.java, MessageOutputModel::toDomain, pageRequest))
        } catch (e: Exception) {
            Log.d("FirestoreMessageRepository", "Failed to get channel messages", e)
            failure(Problem.UnexpectedProblem)
        }
    }

    override suspend fun updateMessages(messages: List<Message>): Either<Problem, Unit> {
        return try {
            val pendingSets =
                messages.map { message ->
                    messageCollection.document(message.id.value.toString())
                        .set(MessageOutputModel.fromDomain(message))
                }
            pendingSets.forEach { it.await() }
            success(Unit)
        } catch (e: Exception) {
            Log.d("FirestoreMessageRepository", "Failed to update messages", e)
            failure(Problem.UnexpectedProblem)
        }
    }
}
