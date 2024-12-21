package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun MessagesList(
    channel: Channel,
    scrollState: InfiniteScrollState<Message>,
    onBottomScroll: () -> Unit,
    onDelete: (Message) -> Unit,
    currentUserId: Identifier,
    onToggleEdit: (Message?) -> Unit,
) {
    InfiniteScroll(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(16.dp),
        reverse = true,
    ) { message ->
        MessageView(
            canEdit = message.author.id == currentUserId,
            canDelete = if (channel.owner.id == currentUserId) true else message.author.id == currentUserId,
            message = message,
            onEdit = onToggleEdit,
            onDelete = onDelete,
            isAuthor = message.author.id == currentUserId,
        )
    }
}
