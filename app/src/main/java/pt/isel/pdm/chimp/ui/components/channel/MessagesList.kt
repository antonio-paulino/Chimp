package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun MessagesList(
    scrollState: InfiniteScrollState<Message>,
    onBottomScroll: () -> Unit,
) {
    InfiniteScroll<Message>(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(16.dp),
        reverse = true,
    ) { message ->
        MessageView(
            message = message,
        )
    }
}