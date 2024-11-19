package pt.isel.pdm.chimp.domain.user

import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name

/**
 * User domain class.
 *
 * @property id The unique identifier of the user.
 * @property name The name of the user.
 * @property email The email of the user.
 */
data class User(
    override val id: Identifier = Identifier(0),
    val name: Name,
    val email: Email,
) : Identifiable
