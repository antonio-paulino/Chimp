package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable

/**
 * Input model for creating a message.
 *
 * @property content The content of the message.
 */
@Serializable
class MessageCreationInputModel(
    val content: String,
)
