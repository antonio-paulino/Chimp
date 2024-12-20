
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.channel.MessagesList
import pt.isel.pdm.chimp.ui.screens.channel.ChannelScreenState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun ChannelScreen(
    state: ChannelScreenState,
    channel: Channel?,
    scrollState: InfiniteScrollState<Message>,
    onBottomScroll: () -> Unit,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onEditChannel: () -> Unit,
    onInviteMember: () -> Unit,
    onChannelDelete: () -> Unit,
    onChannelMembers: () -> Unit,
    onManageInvitations: () -> Unit,
) {
    if (channel == null) {
        onBack()
        return
    }
    ChIMPTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopBar(
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                            Text(channel.name.value)
                        }
                    },
                    actions = {
                        ChannelScreenDropDown(
                            onEditChannel = onEditChannel,
                            onChannelMembers = onChannelMembers,
                            onInviteMember = onInviteMember,
                            onChannelDelete = onChannelDelete,
                            onManageInvitations = onManageInvitations,
                        )
                    },
                )
            },
            bottomBar = {
                BottomBar(
                    state = state,
                    onSendMessage = onSendMessage,
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
                MessagesList(
                    scrollState = scrollState,
                    onBottomScroll = onBottomScroll,
                )
            }
        }
        LaunchedEffect(state) {
            if (state is ChannelScreenState.ChannelMessagesError) {
                snackBarHostState.showSnackbar(
                    SnackBarVisuals(
                        message = state.problem.detail,
                    ),
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    state: ChannelScreenState,
    onSendMessage: (String) -> Unit,
) {
    var message by remember {
        mutableStateOf(
            (state as? ChannelScreenState.EditingMessage)?.message?.content ?: "",
        )
    }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message") },
        )
        IconButton(onClick = {
            onSendMessage(message)
            message = ""
        }) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
        }
    }
}

@Composable
fun ChannelScreenDropDown(
    onChannelDelete: () -> Unit,
    onEditChannel: () -> Unit,
    onChannelMembers: () -> Unit,
    onInviteMember: () -> Unit,
    onManageInvitations: () -> Unit,
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
                        contentDescription = stringResource(R.string.edit_channel),
                    )
                    Text(
                        text = stringResource(R.string.edit_channel),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = {
                onEditChannel()
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
                        contentDescription = stringResource(R.string.channel_members),
                    )
                    Text(
                        text = stringResource(R.string.channel_members),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = { onChannelMembers() },
        )
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.manage_channel_invitations),
                    )
                    Text(
                        text = stringResource(R.string.manage_channel_invitations),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = onManageInvitations,
        )
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.invite_member),
                    )
                    Text(
                        text = stringResource(R.string.invite_member),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = onInviteMember,
        )
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.delete_channel),
                    )
                    Text(
                        text = stringResource(R.string.delete_channel),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = onChannelDelete,
        )
    }
}
