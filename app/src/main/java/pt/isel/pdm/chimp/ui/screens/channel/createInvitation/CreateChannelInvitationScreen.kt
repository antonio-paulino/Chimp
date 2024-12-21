package pt.isel.pdm.chimp.ui.screens.channel.createInvitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.buttons.BackButton
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationInput
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationOptions
import pt.isel.pdm.chimp.ui.components.inputs.RoleInput
import pt.isel.pdm.chimp.ui.components.inputs.SearchBarField
import pt.isel.pdm.chimp.ui.components.users.UserResultsList
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun CreateChannelInvitationScreen(
    channel: Channel?,
    state: CreateChannelInvitationScreenState,
    scrollState: InfiniteScrollState<User>,
    user: User?,
    searchField: String,
    onSearchValueChange: (String) -> Unit,
    doSearch: () -> Unit,
    onScrollToBottom: () -> Unit,
    onInviteUser: (User, ChannelRole, ExpirationOptions) -> Unit,
    onBack: () -> Unit,
) {
    if (channel == null || user == null) {
        onBack()
        return
    }
    val (expiration, setExpiration) = remember { mutableStateOf(state.expiration) }
    val (role, setRole) = remember { mutableStateOf(state.role) }
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopBar(
                modifier = Modifier.height(100.dp),
                content = {
                    BackButton { onBack() }
                },
                actions = {
                    SearchBarField(
                        searchField,
                        onSearchValueChange,
                        doSearch,
                        modifier = Modifier.fillMaxWidth(0.8f).padding(start = 4.dp, end = 40.dp),
                        label = stringResource(id = R.string.search_users),
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoleInput(
                    role = role,
                    onRoleChange = setRole,
                    modifier = Modifier.fillMaxWidth(0.4f),
                )
                ExpirationInput(
                    expiration = expiration,
                    onExpirationChange = setExpiration,
                    modifier = Modifier.fillMaxWidth(0.7f),
                )
            }
            UserResultsList(
                channel = channel,
                user = user,
                state = state,
                scrollState = scrollState,
                onBottomScroll = onScrollToBottom,
                onUserInvite = { user ->
                    onInviteUser(user, role, expiration)
                },
            )
        }
    }

    val invitationSentMessage = stringResource(id = R.string.invitation_sent)
    LaunchedEffect(state) {
        when (state) {
            is CreateChannelInvitationScreenState.SearchingUsersError -> {
                snackBarHostState.showSnackbar(
                    SnackBarVisuals(message = state.problem.detail),
                )
            }
            is CreateChannelInvitationScreenState.InvitationSent -> {
                snackBarHostState.showSnackbar(
                    SnackBarVisuals(message = "$invitationSentMessage ${state.user.name.value}"),
                )
            }
            is CreateChannelInvitationScreenState.SubmittingInvitationError -> {
                snackBarHostState.showSnackbar(
                    SnackBarVisuals(message = state.problem.detail),
                )
            }
            else -> {}
        }
    }
}
