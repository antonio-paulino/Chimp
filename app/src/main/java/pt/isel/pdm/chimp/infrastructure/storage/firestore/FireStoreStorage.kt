package pt.isel.pdm.chimp.infrastructure.storage.firestore

import pt.isel.pdm.chimp.infrastructure.session.ChannelRepository
import pt.isel.pdm.chimp.infrastructure.storage.MessageRepository
import pt.isel.pdm.chimp.infrastructure.storage.Storage

class FireStoreStorage : Storage {
    override val messageRepository: MessageRepository = FireStoreMessageRepository()
    override val channelRepository: ChannelRepository = FireStoreChannelRepository()
}
