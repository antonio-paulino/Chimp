package pt.isel.pdm.chimp.infrastructure.storage

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import java.time.LocalDateTime

/**
 * Repository that provides operations related to messages.
 *
 * This repository is responsible for managing the messages in the Firestore database.
 */
interface MessageRepository {
    /**
     * Gets all the messages for a channel.
     *
     * @param channel The channel where the messages are.
     * @param getCount Whether to count the total number of messages.
     * @param sortDirection The direction of the sort.
     * @param before The date of creation of the last message currently in view.
     */
    suspend fun getChannelMessages(
        channel: Channel,
        limit: Long = 10,
        getCount: Boolean = false,
        sortDirection: Sort = Sort.DESC,
        before: LocalDateTime?,
    ): Either<Problem, Pagination<Message>>

    /**
     * Updates or creates messages in the Firestore database.
     */
    suspend fun updateMessages(messages: List<Message>): Either<Problem, Unit>

    /**
     * Deletes a message from the Firestore database.
     */
    suspend fun deleteMessage(messageIdentifier: Identifier): Either<Problem, Unit>
}
