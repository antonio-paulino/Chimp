package pt.isel.pdm.chimp.dto.input

import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.name.Name

/**
 * Input model for creating a channel.
 *
 * @property name The name of the channel.
 * @property isPublic Whether the channel is public.
 */
data class ChannelCreationInputModel(
    val name: String,
    val defaultRole: String,
    val isPublic: String,
) {
    constructor(
        name: Name,
        defaultRole: ChannelRole,
        isPublic: Boolean,
    ) : this(
        name.value,
        defaultRole.name,
        isPublic.toString(),
    )
}
