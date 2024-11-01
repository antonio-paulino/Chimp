package pt.isel.pdm.chimp.dto.input

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.domain.channel.ChannelRole

/**
 * Input model for updating a channel role.
 */
@Serializable
data class ChannelRoleUpdateInputModel(
    val role: String,
) {
    constructor(role: ChannelRole) : this(role.name)
}
