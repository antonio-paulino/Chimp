package pt.isel.pdm.chimp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.ui.components.NavBar
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
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
    loadMore: () -> Unit,
    onChannelSelected: (Channel) -> Unit,
) {
    if (session == null) {
        onNotLoggedIn()
        return
    } else {
        onLoggedIn(session)
    }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopBar(
                content = { Text(text = stringResource(R.string.home)) },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.create_channel)) },
                            onClick = { TODO() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.invite_user)) },
                            onClick = { TODO() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.logout)) },
                            onClick = { TODO() }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavBar(
                onHomeNavigation = {},
                onSearchNavigation = { TODO() },
                onInvitationsNavigation = { TODO() },
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
                is ChannelScreenState.ChannelsList -> {
                    ChannelsListScreen(
                        scrollState = scrollState,
                        loadMore = loadMore,
                        onChannelSelected = onChannelSelected
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelsListScreen(
    scrollState: InfiniteScrollState<Channel>,
    loadMore: () -> Unit,
    onChannelSelected: (Channel) -> Unit
) {
    InfiniteScroll(
        scrollState = scrollState,
        loadMore = loadMore,
    ) { channel ->
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
                .padding(16.dp)
                .clickable { onChannelSelected(channel) }
        ) {
            ChannelRoleIcon(channel = channel)
            Row {
                Text(text = channel.name.value, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "  ${channel.members.size} members", color = MaterialTheme.colorScheme.onBackground)
            }
            ChannelPrivacyIcon(channel = channel)
        }
    }
}


@Composable
fun ChannelRoleIcon(channel: Channel) {
    when (channel.defaultRole) {
        ChannelRole.MEMBER -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.member_role)
            )
        }
        ChannelRole.GUEST -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.guest_role)
            )
        }
        else -> {
            throw IllegalStateException("Illegal channel role as default: ${channel.defaultRole}")
        }
    }
}

@Composable
fun ChannelPrivacyIcon(channel: Channel) {
    if (!channel.isPublic) {
        Icon(
            tint = MaterialTheme.colorScheme.onBackground,
            imageVector = Icons.Default.Lock,
            contentDescription = stringResource(R.string.private_channel)
        )
    } else {
        // Placeholder for alignment
        Box(
            modifier = Modifier
                .size(24.dp) // Adjust size as needed
                .background(Color.Transparent)
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