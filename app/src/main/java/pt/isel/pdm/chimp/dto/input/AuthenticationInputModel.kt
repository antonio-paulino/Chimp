package pt.isel.pdm.chimp.dto.input

import im.domain.wrappers.email.Email
import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password


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
) {
    companion object {
        operator fun invoke(username: Name?, password: Password, email: Email?) =
            AuthenticationInputModel(username?.value, password.value, email?.value)
    }
}
