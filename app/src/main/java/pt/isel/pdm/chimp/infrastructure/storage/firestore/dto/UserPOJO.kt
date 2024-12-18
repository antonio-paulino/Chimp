package pt.isel.pdm.chimp.infrastructure.storage.firestore.dto

import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.email.toEmail
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier
import pt.isel.pdm.chimp.domain.wrappers.name.toName

data class UserPOJO(
    var id: Long = 0,
    var name: String = "",
    var email: String = "",
) {
    fun toDomain() = User(id.toIdentifier(), name.toName(), email.toEmail())
}
