package pt.isel.pdm.chimp.domain.channel

import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import java.time.LocalDateTime

/**
 * Represents a channel in the application.
 *
 * @property id the unique identifier of the channel
 * @property name the name of the channel
 * @property owner the user that created the channel
 * @property isPublic whether the channel is public or private
 * @property createdAt the date and time when the channel was created
 * @property members the users that are members of the channel and their roles
 */
data class Channel(
    val id: Identifier = Identifier(0),
    val name: Name,
    val defaultRole: ChannelRole,
    val owner: User,
    val isPublic: Boolean,
    val createdAt: LocalDateTime,
    val members: List<ChannelMember>,
)

typealias UserChannels = Pair<OwnedChannels, MemberChannels>

typealias OwnedChannels = List<Channel>

typealias MemberChannels = List<Channel>
