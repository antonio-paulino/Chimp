package pt.isel.pdm.chimp.infrastructure.storage

interface Storage {
    val messageRepository: MessageRepository
    val channelRepository: ChannelRepository
}
