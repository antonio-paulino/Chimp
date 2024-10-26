package pt.isel.pdm.chimp.dto.output.users

import im.domain.wrappers.email.toEmail
import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName

@Serializable
data class UserOutputModel(
    val id: Long,
    val name: String,
    val email: String,
) {
    fun toDomain() = User(id.toIdentifier(), name.toName(), email.toEmail())
}
