package pt.isel.pdm.chimp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@Composable
fun ChannelsScreen(
    session: Session?,
    scrollState: InfiniteScrollState<Channel>,
    channelState: ChannelScreenState,
    onAboutNavigation: () -> Unit,
    onNotLoggedIn: () -> Unit,
    onLoggedIn: (Session) -> Unit,
) {
    if (session == null) {
        onNotLoggedIn()
        return
    } else {
        onLoggedIn(session)
    }
    when (channelState) {
        is ChannelScreenState.CreationError -> {
            // TODO
            Text(text = "Error creating channel: ${channelState.message}")
        }
        is ChannelScreenState.CreatingChannel -> {
            // TODO
            Text(text = "Creating channel: ${channelState.channelName}")
        }
        is ChannelScreenState.ChannelsListAll -> {
            // TODO
            ChannelsListScreen(scrollState = scrollState)
        }
    }
}

@Composable
fun ChannelsListScreen(scrollState: InfiniteScrollState<Channel>) {
    // TODO
    TopBar {
        Text(text = "Channels")
    }
}

@Preview
@Composable
fun ChannelsScreenPreview() {
    ChIMPTheme {
        Text(text = "Channels Screen")
    }
}
