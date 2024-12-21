package pt.isel.pdm.chimp.domain.messages

import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.R

sealed class MessageValidationError(
    private val defaultMessage: String = "Invalid message",
) {
    data object ContentBlank : MessageValidationError(
        ChimpApplication.applicationContext().resources.getString(R.string.message_content_blank),
    )

    data class ContentLength(
        val min: Int,
        val max: Int,
    ) : MessageValidationError(ChimpApplication.applicationContext().resources.getString(R.string.message_content_length, min, max))

    fun toErrorMessage(): String = defaultMessage
}

fun List<MessageValidationError>.toErrorMessage(): String = joinToString("\n") { it.toString() }
