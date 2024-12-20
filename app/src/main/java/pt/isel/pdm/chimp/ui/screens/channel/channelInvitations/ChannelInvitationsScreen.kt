package pt.isel.pdm.chimp.ui.screens.channel.channelInvitations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.buttons.BackButton
import pt.isel.pdm.chimp.ui.components.invitations.ChannelInvitationsManagedList
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun ChannelInvitationsScreen(
    state: ChannelInvitationsScreenState,
    scrollState: InfiniteScrollState<ChannelInvitation>,
    onChannelNull: () -> Unit,
    onRemoveInvitation: (ChannelInvitation) -> Unit,
    onUpdateInvitationRole: (ChannelInvitation, ChannelRole) -> Unit,
    loadMore: () -> Unit,
    onBack: () -> Unit,
    channel: Channel?,
) {
    if (channel == null) {
        onChannelNull()
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
                            BackButton(onBack)
                            Text("${channel.name.value} ${stringResource(id = R.string.invitations)}")
                        }
                    },
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
                ChannelInvitationsManagedList(
                    scrollState = scrollState,
                    onBottomScroll = loadMore,
                    onInvitationDeleted = onRemoveInvitation,
                    onInvitationRoleChanged = onUpdateInvitationRole,
                )
            }
        }

        val invitationRemovedMessage = stringResource(id = R.string.invitation_removed)
        val invitationRoleUpdatedMessage = stringResource(id = R.string.invitation_role_updated)
        LaunchedEffect(state) {
            when (state) {
                is ChannelInvitationsScreenState.ChannelInvitationsListError -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = state.problem.detail),
                    )
                }
                is ChannelInvitationsScreenState.InvitationRemoved -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = "$invitationRemovedMessage ${state.invitation.invitee.name}"),
                    )
                }
                is ChannelInvitationsScreenState.InvitationRoleChanged -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = "$invitationRoleUpdatedMessage ${state.invitation.role}"),
                    )
                }
                else -> {}
            }
        }
    }
}
