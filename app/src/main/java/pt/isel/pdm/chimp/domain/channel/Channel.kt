package pt.isel.pdm.chimp.domain.channel

import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name

/**
 * Represents a channel in the application.
 *
 * @property id the unique identifier of the channel
 * @property name the name of the channel
 * @property owner the user that created the channel
 * @property isPublic whether the channel is public or private
 * @property members the users that are members of the channel and their roles
 */
data class Channel(
    override val id: Identifier = Identifier(0),
    val name: Name,
    val defaultRole: ChannelRole,
    val owner: User,
    val isPublic: Boolean,
    val members: List<ChannelMember>,
) : Identifiable
