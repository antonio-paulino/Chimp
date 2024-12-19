package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun ChannelResultsList(
    scrollState: InfiniteScrollState<Channel>,
    onBottomScroll: () -> Unit,
    onJoinChannel: (Channel) -> Unit,
    user: User,
) {
    InfiniteScroll(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(16.dp),
        filterCondition = { channel -> channel.members.none { it.id == user.id } },
        reverse = false,
    ) { channel ->
        ChannelResultView(
            channel = channel,
            onJoinChannel = { onJoinChannel(channel) },
        )
    }
}
