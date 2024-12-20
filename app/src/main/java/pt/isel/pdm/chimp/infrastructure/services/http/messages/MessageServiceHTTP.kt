package pt.isel.pdm.chimp.infrastructure.services.http.messages

import io.ktor.client.HttpClient
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.dto.input.MessageCreationInputModel
import pt.isel.pdm.chimp.dto.output.messages.MessageCreationOutputModel
import pt.isel.pdm.chimp.dto.output.messages.MessageOutputModel
import pt.isel.pdm.chimp.dto.output.messages.MessageUpdateOutputModel
import pt.isel.pdm.chimp.dto.output.messages.MessagesPaginatedOutputModel
import pt.isel.pdm.chimp.infrastructure.services.http.BaseHTTPService
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_MESSAGES_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.CHANNEL_MESSAGE_ROUTE
import pt.isel.pdm.chimp.infrastructure.services.http.MESSAGE_ID_PARAM
import pt.isel.pdm.chimp.infrastructure.services.http.buildQuery
import pt.isel.pdm.chimp.infrastructure.services.http.handle
import pt.isel.pdm.chimp.infrastructure.services.interfaces.messages.MessageService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import java.time.LocalDateTime

typealias MessageEditedTime = LocalDateTime

/**
 * HTTP implementation of the [MessageService].
 */
class MessageServiceHTTP(baseURL: String, httpClient: HttpClient) :
    BaseHTTPService(httpClient, baseURL), MessageService {
    /**
     * The implementation of the [MessageService.getChannelMessages] method.
     *
     * @param channel The channel to get the messages from.
     * @param session The session of the user getting the messages.
     * @param pagination The pagination information.
     * @param sort The sorting information.
     * @param before The date to get the messages before.
     *
     * @return An [Either] containing the [Pagination] of [Message] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun getChannelMessages(
        channel: Channel,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
        before: LocalDateTime?,
    ): Either<Problem, Pagination<Message>> =
        get<MessagesPaginatedOutputModel>(
            CHANNEL_MESSAGES_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .plus(buildQuery(null, pagination, sort, before = before)),
            session.accessToken.token.toString(),
        ).handle {
            it.toDomain()
        }

    /**
     * The implementation of the [MessageService.getMessage] method.
     *
     * @param channel The channel to get the message from.
     * @param messageId The identifier of the message.
     * @param session The session of the user getting the message.
     *
     * @return An [Either] containing the [Message] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun getMessage(
        channel: Channel,
        messageId: Identifier,
        session: Session,
    ): Either<Problem, Message> {
        return get<MessageOutputModel>(
            CHANNEL_MESSAGE_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(MESSAGE_ID_PARAM, messageId.value.toString()),
            session.accessToken.token.toString(),
        ).handle {
            it.toDomain()
        }
    }

    /**
     * The implementation of the [MessageService.createMessage] method.
     *
     * @param channel The channel to create the message in.
     * @param content The content of the message.
     * @param session The session of the user creating the message.
     *
     * @return An [Either] containing the [Message] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun createMessage(
        channel: Channel,
        content: String,
        session: Session,
    ): Either<Problem, Message> {
        return post<MessageCreationInputModel, MessageCreationOutputModel>(
            CHANNEL_MESSAGES_ROUTE.replace(CHANNEL_ID_PARAM, channel.id.value.toString()),
            session.accessToken.token.toString(),
            MessageCreationInputModel(content),
        ).handle { it.toDomain(channel, session.user, content) }
    }

    /**
     * The implementation of the [MessageService.updateMessage] method.
     *
     * @param message The message to update.
     * @param content The new content of the message.
     * @param session The session of the user updating the message.
     *
     * @return An [Either] containing the [MessageEditedTime] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun updateMessage(
        message: Message,
        content: String,
        session: Session,
    ): Either<Problem, MessageEditedTime> {
        return put<MessageCreationInputModel, MessageUpdateOutputModel>(
            CHANNEL_MESSAGE_ROUTE
                .replace(CHANNEL_ID_PARAM, message.channelId.value.toString())
                .replace(MESSAGE_ID_PARAM, message.id.toString()),
            session.accessToken.token.toString(),
            MessageCreationInputModel(content),
        ).handle { LocalDateTime.parse(it.editedAt) }
    }

    /**
     * The implementation of the [MessageService.deleteMessage] method.
     *
     * @param message The message to delete.
     * @param session The session of the user deleting the message.
     *
     * @return An [Either] containing [Unit] if the operation was successful or a [Problem] if it was not.
     */
    override suspend fun deleteMessage(
        message: Message,
        session: Session,
    ): Either<Problem, Unit> {
        return delete(
            CHANNEL_MESSAGE_ROUTE
                .replace(CHANNEL_ID_PARAM, message.channelId.value.toString())
                .replace(MESSAGE_ID_PARAM, message.id.value.toString()),
            session.accessToken.token.toString(),
        ).handle { }
    }
}
