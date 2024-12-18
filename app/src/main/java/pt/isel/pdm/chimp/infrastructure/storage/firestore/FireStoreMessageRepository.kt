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
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.dto.output.messages.MessageOutputModel
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.storage.MessageRepository
import pt.isel.pdm.chimp.infrastructure.storage.firestore.dto.MessagePOJO
import java.time.LocalDateTime

class FireStoreMessageRepository : MessageRepository {
    private val db = Firebase.firestore
    private val messageCollection = db.collection("messages")

    override suspend fun getChannelMessages(
        channel: Channel,
        limit: Long,
        getCount: Boolean,
        sortDirection: Sort,
        before: LocalDateTime?,
    ): Either<Problem, Pagination<Message>> {
        return try {
            val querySnapshot =
                messageCollection
                    .whereEqualTo("channelId", channel.id.value)
                    .orderBy("createdAt", sortDirection.toFirestoreSort())
                    .whereLessThan("createdAt", before ?: LocalDateTime.now())
                    .limit(limit)
                    .get().await()
            return success(querySnapshot.getPagination(MessagePOJO::class.java, MessagePOJO::toDomain, limit, getCount))
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

    override suspend fun deleteMessage(messageIdentifier: Identifier): Either<Problem, Unit> {
        return try {
            messageCollection.document(messageIdentifier.value.toString())
                .delete()
                .await()
            success(Unit)
        } catch (e: Exception) {
            Log.d("FirestoreMessageRepository", "Failed to delete message", e)
            failure(Problem.UnexpectedProblem)
        }
    }
}
