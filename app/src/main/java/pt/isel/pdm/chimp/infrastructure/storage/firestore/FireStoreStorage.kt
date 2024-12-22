package pt.isel.pdm.chimp.infrastructure.storage.firestore

import android.content.Context
import pt.isel.pdm.chimp.infrastructure.storage.ChannelRepository
import pt.isel.pdm.chimp.infrastructure.storage.MessageRepository
import pt.isel.pdm.chimp.infrastructure.storage.Storage

class FireStoreStorage(
    context: Context,
) : Storage {
    override val messageRepository: MessageRepository = FireStoreMessageRepository(context)
    override val channelRepository: ChannelRepository = FireStoreChannelRepository(context)
}
