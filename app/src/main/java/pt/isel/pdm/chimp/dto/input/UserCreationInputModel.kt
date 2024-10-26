package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable

/**
 * Input model for creating a user.
 *
 * @property username The username of the user.
 * @property password The password of the user.
 * @property email The email of the user.
 * @property invitation The invitation code for the user.
 */
@Serializable
data class UserCreationInputModel(
    val username: String,
    val password: String,
    val email: String,
    val invitation: String,
) {
    companion object {
        operator fun invoke(username: String, password: String, email: String, invitation: String) =
            UserCreationInputModel(username, password, email, invitation)
    }
}