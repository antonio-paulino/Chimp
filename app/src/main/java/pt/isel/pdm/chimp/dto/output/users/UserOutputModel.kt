package pt.isel.pdm.chimp.dto.output.users

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.email.toEmail
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName

/**
 * Output model for a user, received from the server.
 *
 * @property id The identifier of the user.
 * @property name The name of the user.
 * @property email The email of the user.
 */
@Serializable
data class UserOutputModel(
    val id: Long,
    val name: String,
    val email: String,
) {
    fun toDomain() = User(id.toIdentifier(), name.toName(), email.toEmail())

    companion object {
        fun fromDomain(user: User) = UserOutputModel(user.id.value, user.name.value, user.email.value)
    }
}
