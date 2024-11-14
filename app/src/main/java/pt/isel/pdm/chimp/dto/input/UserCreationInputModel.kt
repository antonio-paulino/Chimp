package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password

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
        operator fun invoke(
            username: Name,
            password: Password,
            email: Email,
            invitation: ImInvitation,
        ) = UserCreationInputModel(username.value, password.value, email.value, invitation.token.toString())
    }
}
