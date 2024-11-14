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
    override suspend fun getChannelMessages(
        channel: Channel,
        session: Session,
        pagination: PaginationRequest?,
        sort: SortRequest?,
    ): Either<Problem, Pagination<Message>> =
        get<MessagesPaginatedOutputModel>(
            CHANNEL_MESSAGES_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .plus(buildQuery(null, pagination, sort)),
            session.accessToken.token.toString(),
        ).handle {
            it.toDomain()
        }

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

    override suspend fun updateMessage(
        channel: Channel,
        messageId: Identifier,
        content: String,
        session: Session,
    ): Either<Problem, MessageEditedTime> {
        return put<MessageCreationInputModel, MessageUpdateOutputModel>(
            CHANNEL_MESSAGE_ROUTE
                .replace(CHANNEL_ID_PARAM, channel.id.value.toString())
                .replace(MESSAGE_ID_PARAM, messageId.value.toString()),
            session.accessToken.token.toString(),
            MessageCreationInputModel(content),
        ).handle { LocalDateTime.parse(it.editedAt) }
    }

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

    companion object {
        private const val CHANNEL_ID_PARAM = "{channelId}"
        private const val MESSAGE_ID_PARAM = "{messageId}"
        private const val CHANNEL_MESSAGES_ROUTE = "channels/$CHANNEL_ID_PARAM/messages"
        private const val CHANNEL_MESSAGE_ROUTE = "channels/$CHANNEL_ID_PARAM/messages/$MESSAGE_ID_PARAM"
    }
}
