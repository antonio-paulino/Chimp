package pt.isel.pdm.chimp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.ui.components.NavBar
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.channel.ChannelsList
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun ChannelsScreen(
    session: Session?,
    scrollState: InfiniteScrollState<Channel>,
    channelState: ChannelScreenState,
    onNotLoggedIn: () -> Unit,
    onLoggedIn: (Session) -> Unit,
    onBottomScroll: () -> Unit,
    onChannelSelected: (Channel) -> Unit,
    onAboutNavigation: () -> Unit,
    onInvitationsNavigation: () -> Unit,
    onLogout: () -> Unit,
    onCreateChannelNavigation: () -> Unit,
    onInviteUserNavigation: () -> Unit,
) {
    if (session == null) {
        onNotLoggedIn()
        return
    } else {
        onLoggedIn(session)
    }
    ChIMPTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopBar(
                    content = { Text(text = stringResource(R.string.home)) },
                    actions = {
                        ChannelsScreenDropDown(
                            onLogout = onLogout,
                            onCreateChannel = onCreateChannelNavigation,
                            onInviteUserNavigation = onInviteUserNavigation,
                        )
                    },
                )
            },
            bottomBar = {
                NavBar(
                    onHomeNavigation = {},
                    onSearchNavigation = { TODO() },
                    onInvitationsNavigation = onInvitationsNavigation,
                    onAboutNavigation = onAboutNavigation,
                    currentScreen = stringResource(R.string.home),
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
                when (channelState) {
                    is ChannelScreenState.ChannelsList,
                    is ChannelScreenState.ChannelsListError,
                    -> {
                        ChannelsList(
                            scrollState = scrollState,
                            onBottomScroll = onBottomScroll,
                            onChannelSelected = onChannelSelected,
                        )
                    }
                }
            }
        }
        LaunchedEffect(channelState) {
            if (channelState is ChannelScreenState.ChannelsListError) {
                snackBarHostState.showSnackbar(
                    SnackBarVisuals(
                        message = channelState.problem.detail
                    ),
                )
            }
        }
    }
}

@Composable
fun ChannelsScreenDropDown(
    onLogout: () -> Unit,
    onCreateChannel: () -> Unit,
    onInviteUserNavigation: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.more_options),
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.create_channel),
                    )
                    Text(
                        text = stringResource(R.string.create_channel),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = {
                onCreateChannel()
                expanded = false
            },
        )
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.invite_user),
                    )
                    Text(
                        text = stringResource(R.string.invite_user),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = { onInviteUserNavigation() },
        )
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.logout),
                    )
                    Text(
                        text = stringResource(R.string.logout),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = onLogout,
        )
    }
}

@Preview
@Composable
fun ChannelsScreenPreview() {
    ChIMPTheme {
        Text(text = "Channels Screen")
    }
}
