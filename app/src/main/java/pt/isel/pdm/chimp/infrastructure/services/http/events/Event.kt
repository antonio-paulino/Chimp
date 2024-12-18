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
internal const val CHANNEL_CREATED_EVENT = "channel-created"
internal const val CHANNEL_DELETED_EVENT = "channel-deleted"
internal const val CHANNEL_UPDATED_EVENT = "channel-updated"
internal const val KEEP_ALIVE_EVENT = "keep-alive"

typealias JsonString = String
typealias EventId = String

private const val READ_DELAY = 100L

internal fun ByteReadChannel.readEvents(scope: CoroutineScope): Flow<Event> {
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

/**
 * Strongly typed representation of an event received from the server.
 *
 * @param id The event's identifier.
 */
sealed class Event(val id: EventId) {
    data class KeepAliveEvent(val eventId: EventId) : Event(eventId)

    sealed class MessageEvent(messageEventId: EventId, messageId: Identifier) : Event(messageEventId) {
        data class CreatedEvent(val eventId: EventId, val message: Message) : MessageEvent(eventId, message.id)

        data class UpdatedEvent(val eventId: EventId, val message: Message) : MessageEvent(eventId, message.id)

        data class DeletedEvent(val eventId: EventId, val messageId: Identifier) : MessageEvent(eventId, messageId)
    }

    sealed class InvitationEvent(invitationEventId: EventId, invitationId: Identifier) : Event(invitationEventId) {
        data class CreatedEvent(val eventId: EventId, val invitation: ChannelInvitation) : InvitationEvent(eventId, invitation.id)

        data class UpdatedEvent(val eventId: EventId, val invitation: ChannelInvitation) : InvitationEvent(eventId, invitation.id)

        data class DeletedEvent(val eventId: EventId, val invitationId: Identifier) : InvitationEvent(eventId, invitationId)
    }

    sealed class ChannelEvent(channelEventId: EventId, channelId: Identifier) : Event(channelEventId) {
        data class CreatedEvent(val eventId: EventId, val channel: Channel) : ChannelEvent(eventId, channel.id)

        data class DeletedEvent(val eventId: EventId, val channelId: Identifier) : ChannelEvent(eventId, channelId)

        data class UpdatedEvent(val eventId: EventId, val channel: Channel) : ChannelEvent(eventId, channel.id)
    }
}

internal fun RawEvent.toEvent(): Event {
    return when (type) {
        MESSAGE_CREATED_EVENT -> Event.MessageEvent.CreatedEvent(id, data.toMessage())
        MESSAGE_UPDATED_EVENT -> Event.MessageEvent.UpdatedEvent(id, data.toMessage())
        MESSAGE_DELETED_EVENT -> Event.MessageEvent.DeletedEvent(id, data.toIdentifier())
        INVITATION_CREATED_EVENT -> Event.InvitationEvent.CreatedEvent(id, data.toInvitation())
        INVITATION_UPDATED_EVENT -> Event.InvitationEvent.UpdatedEvent(id, data.toInvitation())
        INVITATION_DELETED_EVENT -> Event.InvitationEvent.DeletedEvent(id, data.toIdentifier())
        CHANNEL_CREATED_EVENT -> Event.ChannelEvent.CreatedEvent(id, data.toChannel())
        CHANNEL_DELETED_EVENT -> Event.ChannelEvent.DeletedEvent(id, data.toIdentifier())
        CHANNEL_UPDATED_EVENT -> Event.ChannelEvent.UpdatedEvent(id, data.toChannel())
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
