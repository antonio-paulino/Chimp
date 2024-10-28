package pt.isel.pdm.chimp.dto.output.users

import pt.isel.pdm.chimp.domain.user.User

class UserCreationOutputModel(
    val id: Long,
) {
    companion object {
        fun fromDomain(user: User): UserCreationOutputModel =
            UserCreationOutputModel(
                id = user.id.value,
            )
    }
}
