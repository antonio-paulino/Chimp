package pt.isel.pdm.chimp.ui.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.NavBar
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.channel.searchResults.ChannelResultsList
import pt.isel.pdm.chimp.ui.components.inputs.ChannelSearchBar
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun ChannelSearchScreen(
    onNotLoggedIn: () -> Unit,
    state: ChannelSearchListScreenState,
    scrollState: InfiniteScrollState<Channel>,
    user: User?,
    searchField: String,
    onSearchValueChange: (String) -> Unit,
    doSearch: () -> Unit,
    onScrollToBottom: () -> Unit,
    onJoinChannel: (Channel) -> Unit,
    onHomeNavigation: () -> Unit,
    onInvitationsNavigation: () -> Unit,
    onAboutNavigation: () -> Unit,
) {
    if (user == null) {
        onNotLoggedIn()
        return
    }
    ChIMPTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopBar(
                    modifier = Modifier.height(100.dp),
                    content = { ChannelSearchBar(searchField, onSearchValueChange, doSearch) },
                )
            },
            bottomBar = {
                NavBar(
                    onHomeNavigation = onHomeNavigation,
                    onSearchNavigation = {},
                    onInvitationsNavigation = onInvitationsNavigation,
                    onAboutNavigation = onAboutNavigation,
                    currentScreen = stringResource(R.string.search),
                )
            },
            containerColor = Color.Transparent,
        ) { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                ChannelResultsList(
                    scrollState = scrollState,
                    onBottomScroll = onScrollToBottom,
                    onJoinChannel = onJoinChannel,
                    user = user,
                )
            }
        }

        val joinedChannelMessage = stringResource(R.string.joined_channel)
        LaunchedEffect(state) {
            when (state) {
                is ChannelSearchListScreenState.ChannelSearchListError -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = state.problem.detail),
                    )
                }
                is ChannelSearchListScreenState.JoinedChannel -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = joinedChannelMessage + " ${state.channel.name}"),
                    )
                }
                else -> {}
            }
        }
    }
}
