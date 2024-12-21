package pt.isel.pdm.chimp.domain.channel

import pt.isel.pdm.chimp.domain.Identifiable
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
 * @property members the users that are members of the channel and their roles
 */
data class Channel(
    override val id: Identifier = Identifier(0),
    val name: Name,
    val defaultRole: ChannelRole,
    val owner: User,
    val isPublic: Boolean,
    val members: List<ChannelMember>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) : Identifiable {
    fun getMemberRole(user: User): ChannelRole? {
        return members.find { it.id == user.id }?.role
    }

    fun isMember(user: User): Boolean {
        return members.any { it.id == user.id }
    }

    fun isOwner(user: User): Boolean {
        return owner.id == user.id
    }

    fun isGuest(user: User): Boolean {
        return getMemberRole(user) == ChannelRole.GUEST
    }

    fun getMember(user: User): ChannelMember? {
        return members.find { it.id == user.id }
    }
}
