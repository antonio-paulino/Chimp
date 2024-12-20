package pt.isel.pdm.chimp.dto.output.users

import kotlinx.serialization.Serializable

/**
 * Output model for the creation of a user, received from the server.
 *
 * @property id The identifier of the created user.
 */
@Serializable
class UserCreationOutputModel(
    val id: Long,
)
