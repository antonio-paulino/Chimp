package pt.isel.pdm.chimp.infrastructure.services.interfaces.messages

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.infrastructure.services.http.messages.MessageEditedTime
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/**
 * Service that provides operations related to messages.
 */
interface MessageService {
    /**
     * Gets all the messages for a channel.
     *
     * - The user must be a member of the channel to see the messages.
     *
     * @param channel The channel where the messages are.
     * @param pagination The pagination information.
     * @param sort The sort information.
     * @param session The session of the user.
     */
    suspend fun getChannelMessages(
        channel: Channel,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<Message>>

    /**
     * Gets a message from a channel.
     *
     * - The user must be a member of the channel to see the message.
     *
     * @param channel The channel where the message is.
     * @param messageId The identifier of the message.
     * @param session The session of the user.
     */
    suspend fun getMessage(
        channel: Channel,
        messageId: Identifier,
        session: Session,
    ): Either<Problem, Message>

    /**
     * Creates a message in a channel.
     *
     * - The user must be a member of the channel to create a message.
     *
     * @param channel The channel where the message is.
     * @param content The content of the message.
     * @param session The session of the user.
     *
     * @return Either a [Problem] or the created message.
     */
    suspend fun createMessage(
        channel: Channel,
        content: String,
        session: Session,
    ): Either<Problem, Message>

    /**
     * Updates a message in a channel.
     *
     * - The user must be the author of the message to update it.
     *
     * @param channel The channel where the message is.
     * @param messageId The identifier of the message.
     * @param content The new content for the message.
     * @param session The session of the user.
     */
    suspend fun updateMessage(
        channel: Channel,
        messageId: Identifier,
        content: String,
        session: Session,
    ): Either<Problem, MessageEditedTime>

    /**
     * Deletes a message from a channel.
     *
     * - The user must be the author of the message or the owner of the channel to delete it.
     *
     * @param message The message to delete.
     * @param session The session of the user.
     */
    suspend fun deleteMessage(
        message: Message,
        session: Session,
    ): Either<Problem, Unit>
}
