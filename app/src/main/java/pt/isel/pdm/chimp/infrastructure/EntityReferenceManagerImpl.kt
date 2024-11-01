package pt.isel.pdm.chimp.infrastructure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.user.User

class EntityReferenceManagerImpl : EntityReferenceManager {
    override var message: Message? by mutableStateOf(null)
        private set

    override var channel: Channel? by mutableStateOf(null)
        private set

    override var user: User? by mutableStateOf(null)
        private set

    override var invitation: ChannelInvitation? by mutableStateOf(null)
        private set

    override fun set(message: Message) {
        this.message = message
    }

    override fun set(channel: Channel) {
        this.channel = channel
    }

    override fun set(user: User) {
        this.user = user
    }

    override fun set(invitation: ChannelInvitation) {
        this.invitation = invitation
    }
}
