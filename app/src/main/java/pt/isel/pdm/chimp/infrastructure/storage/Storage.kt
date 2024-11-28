package pt.isel.pdm.chimp.infrastructure.storage

import pt.isel.pdm.chimp.infrastructure.session.ChannelRepository

interface Storage {
    val messageRepository: MessageRepository
    val channelRepository: ChannelRepository
}
