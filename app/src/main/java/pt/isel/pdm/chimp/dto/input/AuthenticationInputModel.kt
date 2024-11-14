package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable

/**
 * Input model for login.
 *
 * A user can log in using either their username or email.
 *
 * @property username The username of the user.
 * @property password The password of the user.
 * @property email The email of the user.
 */
@Serializable
data class AuthenticationInputModel(
    val username: String?,
    val password: String,
    val email: String?,
)
