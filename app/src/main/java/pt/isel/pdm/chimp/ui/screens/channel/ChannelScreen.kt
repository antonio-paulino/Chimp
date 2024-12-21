
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.messages.MessageValidator
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.channel.MessagesList
import pt.isel.pdm.chimp.ui.screens.channel.ChannelScreenState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun ChannelScreen(
    state: ChannelScreenState,
    session: Session?,
    channel: Channel?,
    scrollState: InfiniteScrollState<Message>,
    onBottomScroll: () -> Unit,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onEditMessage: (Message, String) -> Unit,
    onDeleteMessage: (Message) -> Unit,
    onEditChannel: () -> Unit,
    onInviteMember: () -> Unit,
    onChannelDelete: () -> Unit,
    onChannelMembers: () -> Unit,
    onManageInvitations: () -> Unit,
    onLeaveChannel: () -> Unit,
    onToggleEdit: (Message?) -> Unit,
) {
    if (channel == null) {
        onBack()
        return
    }
    val isOwner = session?.user?.id == channel.owner.id
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
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                            Text(
                                text = channel.name.value,
                                style =
                                    TextStyle(
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                            )
                        }
                    },
                    actions = {
                        ChannelScreenDropDown(
                            onChannelDelete = onChannelDelete,
                            onEditChannel = onEditChannel,
                            onChannelMembers = onChannelMembers,
                            onInviteMember = onInviteMember,
                            onManageInvitations = onManageInvitations,
                            onLeaveChannel = onLeaveChannel,
                            isOwner = isOwner,
                        )
                    },
                )
            },
            bottomBar = {
                BottomBar(
                    state = state,
                    onSendMessage = onSendMessage,
                    onEditMessage = onEditMessage,
                    onToggleEdit = onToggleEdit,
                    modifier = if (state is ChannelScreenState.EditingMessage)Modifier.height(160.dp) else Modifier,
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
                    channel = channel,
                    scrollState = scrollState,
                    onBottomScroll = onBottomScroll,
                    onToggleEdit = onToggleEdit,
                    onDelete = onDeleteMessage,
                    currentUserId = session?.user?.id!!,
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
    modifier: Modifier,
    state: ChannelScreenState,
    onSendMessage: (String) -> Unit,
    onEditMessage: (Message, String) -> Unit,
    onToggleEdit: (Message?) -> Unit,
) {
    var message by remember { mutableStateOf("") }
    val messageValidator = MessageValidator()
    BottomAppBar(
        modifier = modifier.padding(4.dp),
        contentPadding = PaddingValues(2.dp),
        containerColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state is ChannelScreenState.EditingMessage) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Editing message",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(start = 4.dp),
                    )
                    IconButton(onClick = {
                        onToggleEdit(null)
                        message = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel Edit",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                    maxLines = 2,
                    textStyle =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    placeholder = {
                        Text(
                            text = "Type a message",
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                )

                IconButton(
                    onClick = {
                        if (state !is ChannelScreenState.EditingMessage) {
                            onSendMessage(message.trim())
                            message = ""
                        } else {
                            onEditMessage(state.message, message.trim())
                            message = ""
                            onToggleEdit(null)
                        }
                    },
                    enabled = messageValidator.validate(message) is Success,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint =
                            if (messageValidator.validate(message) is Success) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            },
                    )
                }
            }
        }
    }

    LaunchedEffect(state) {
        if (state is ChannelScreenState.EditingMessage) {
            message = state.message.content
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
    onLeaveChannel: () -> Unit,
    isOwner: Boolean = false,
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
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.channel_members),
                    )
                    Text(
                        text = stringResource(R.string.channel_members),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            onClick = {
                onChannelMembers()
                expanded = false
            },
        )
        if (isOwner) {
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
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
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.manage_channel_invitations),
                        )
                        Text(
                            text = stringResource(R.string.manage_channel_invitations),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                onClick = {
                    onManageInvitations()
                    expanded = false
                },
            )
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(R.string.invite_member),
                        )
                        Text(
                            text = stringResource(R.string.invite_member),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                onClick = {
                    onInviteMember()
                    expanded = false
                },
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
                onClick = {
                    onChannelDelete()
                    expanded = false
                },
            )
        } else {
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.leave_channel),
                        )
                        Text(
                            text = stringResource(R.string.leave_channel),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                onClick = {
                    onLeaveChannel()
                    expanded = false
                },
            )
        }
    }
}
