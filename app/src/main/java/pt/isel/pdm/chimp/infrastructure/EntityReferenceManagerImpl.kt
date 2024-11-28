package pt.isel.pdm.chimp.infrastructure

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.user.User

class EntityReferenceManagerImpl : EntityReferenceManager {
    private val _message = MutableStateFlow<Message?>(null)
    private val _channel = MutableStateFlow<Channel?>(null)
    private val _user = MutableStateFlow<User?>(null)
    private val _invitation = MutableStateFlow<ChannelInvitation?>(null)

    override val message: Flow<Message?> = _message
    override val channel: Flow<Channel?> = _channel
    override val user: Flow<User?> = _user
    override val invitation: Flow<ChannelInvitation?> = _invitation

    override fun set(message: Message?) {
        _message.value = message
    }

    override fun set(channel: Channel?) {
        _channel.value = channel
    }

    override fun set(user: User?) {
        _user.value = user
    }

    override fun set(invitation: ChannelInvitation?) {
        _invitation.value = invitation
    }
}
