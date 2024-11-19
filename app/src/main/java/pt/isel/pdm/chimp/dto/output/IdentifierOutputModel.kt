package pt.isel.pdm.chimp.dto.output

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.wrappers.identifier.toIdentifier

@Serializable
data class IdentifierOutputModel(
    val id: Long,
) {
    fun toDomain() = id.toIdentifier()
}
