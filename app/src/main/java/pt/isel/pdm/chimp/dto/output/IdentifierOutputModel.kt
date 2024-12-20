package pt.isel.pdm.chimp.dto.output

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier

/**
 * Output model for an identifier, received from the server.
 *
 * @property id The identifier.
 */
@Serializable
data class IdentifierOutputModel(
    val id: Long,
) {
    fun toDomain() = id.toIdentifier()
}
