package pt.isel.pdm.chimp.infrastructure.services.http.events

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line

internal data class RawEvent(
    val id: String,
    val type: String,
    val data: String,
)

internal suspend fun ByteReadChannel.readRawEvent(id: String): RawEvent {
    val type = readUTF8Line()?.removePrefix("event:") ?: throw IllegalStateException("Invalid event: missing type")
    val data = readUTF8Line()?.removePrefix("data:") ?: throw IllegalStateException("Invalid event: missing data")
    return RawEvent(id.removePrefix("id:"), type, data)
}