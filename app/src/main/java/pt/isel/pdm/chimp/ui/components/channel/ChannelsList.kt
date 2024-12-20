package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun ChannelsList(
    scrollState: InfiniteScrollState<Channel>,
    onBottomScroll: () -> Unit,
    onChannelSelected: (Channel) -> Unit,
    user: User,
) {
    InfiniteScroll(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(16.dp),
        itemSpacing = PaddingValues(top = 16.dp),
        reverse = false,
    ) { channel ->
        ChannelView(
            channel = channel,
            onChannelSelected = { onChannelSelected(channel) },
            user = user,
        )
    }
}
