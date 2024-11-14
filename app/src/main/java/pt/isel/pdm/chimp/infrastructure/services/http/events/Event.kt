package pt.isel.pdm.chimp.infrastructure.services.http.events

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.dto.output.IdentifierOutputModel
import pt.isel.pdm.chimp.dto.output.channel.ChannelOutputModel
import pt.isel.pdm.chimp.dto.output.invitations.ChannelInvitationOutputModel
import pt.isel.pdm.chimp.dto.output.messages.MessageOutputModel

internal const val MESSAGE_CREATED_EVENT = "message-created"
internal const val MESSAGE_UPDATED_EVENT = "message-updated"
internal const val MESSAGE_DELETED_EVENT = "message-deleted"
internal const val INVITATION_CREATED_EVENT = "invitation-created"
internal const val INVITATION_UPDATED_EVENT = "invitation-updated"
internal const val INVITATION_DELETED_EVENT = "invitation-deleted"
internal const val CHANNEL_DELETED_EVENT = "channel-deleted"
internal const val CHANNEL_UPDATED_EVENT = "channel-updated"
internal const val KEEP_ALIVE_EVENT = "keep-alive"

typealias JsonString = String
typealias EventId = String

private const val READ_DELAY = 1000L

fun ByteReadChannel.readEvents(scope: CoroutineScope): Flow<Event> {
    return flow {
        while (!isClosedForRead && scope.isActive) {
            val line = readUTF8Line()
            if (line == null) {
                delay(READ_DELAY)
                continue
            }
            if (line.isBlank()) continue
            emit(readRawEvent(line).toEvent())
        }
    }
}

sealed class Event(val id : EventId) {
    data class MessageCreatedEvent(val eventId: EventId, val message: Message) : Event(eventId)
    data class MessageUpdatedEvent(val eventId: EventId, val message: Message) : Event(eventId)
    data class MessageDeletedEvent(val eventId: EventId, val messageId: Identifier) : Event(eventId)
    data class InvitationCreatedEvent(val eventId: EventId, val invitation: ChannelInvitation) : Event(eventId)
    data class InvitationUpdatedEvent(val eventId: EventId, val invitation: ChannelInvitation) : Event(eventId)
    data class InvitationDeletedEvent(val eventId: EventId, val invitationId: Identifier) : Event(eventId)
    data class ChannelDeletedEvent(val eventId: EventId, val channelId: Identifier) : Event(eventId)
    data class ChannelUpdatedEvent(val eventId: EventId, val channel: Channel) : Event(eventId)
    data class KeepAliveEvent(val eventId: EventId) : Event(eventId)
}

internal fun RawEvent.toEvent(): Event {
    return when (type) {
        MESSAGE_CREATED_EVENT -> Event.MessageCreatedEvent(id, data.toMessage())
        MESSAGE_UPDATED_EVENT -> Event.MessageUpdatedEvent(id, data.toMessage())
        MESSAGE_DELETED_EVENT -> Event.MessageDeletedEvent(id, data.toIdentifier())
        INVITATION_CREATED_EVENT -> Event.InvitationCreatedEvent(id, data.toInvitation())
        INVITATION_UPDATED_EVENT -> Event.InvitationUpdatedEvent(id, data.toInvitation())
        INVITATION_DELETED_EVENT -> Event.InvitationDeletedEvent(id, data.toIdentifier())
        CHANNEL_DELETED_EVENT -> Event.ChannelDeletedEvent(id, data.toIdentifier())
        CHANNEL_UPDATED_EVENT -> Event.ChannelUpdatedEvent(id, data.toChannel())
        KEEP_ALIVE_EVENT -> Event.KeepAliveEvent(id)
        else -> throw IllegalArgumentException("Unknown event type: $type")
    }
}

private fun JsonString.toMessage(): Message {
    return Json.decodeFromString(MessageOutputModel.serializer(), this).toDomain()
}

private fun JsonString.toInvitation(): ChannelInvitation {
    return Json.decodeFromString(ChannelInvitationOutputModel.serializer(), this).toDomain()
}

private fun JsonString.toChannel(): Channel {
    return Json.decodeFromString(ChannelOutputModel.serializer(), this).toDomain()
}

private fun JsonString.toIdentifier(): Identifier {
    return Json.decodeFromString(IdentifierOutputModel.serializer(), this).toDomain()
}