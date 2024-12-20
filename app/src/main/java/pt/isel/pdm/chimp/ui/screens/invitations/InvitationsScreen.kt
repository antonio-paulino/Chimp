package pt.isel.pdm.chimp.ui.screens.invitations

import androidx.compose.foundation.layout.Box
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
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.ui.components.NavBar
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.invitations.ChannelInvitationsList
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun InvitationsScreen(
    state: InvitationsScreenState,
    scrollState: InfiniteScrollState<ChannelInvitation>,
    onAcceptInvitation: (ChannelInvitation) -> Unit,
    onRejectInvitation: (ChannelInvitation) -> Unit,
    onScrollToBottom: () -> Unit,
    onAboutNavigation: () -> Unit,
    onHomeNavigation: () -> Unit,
    onSearchNavigation: () -> Unit,
) {
    ChIMPTheme {
        val snackBarHostState = remember { SnackbarHostState() }

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopBar(
                    content = { Text(text = stringResource(R.string.invitations)) },
                )
            },
            bottomBar = {
                NavBar(
                    onHomeNavigation = onHomeNavigation,
                    onSearchNavigation = onSearchNavigation,
                    onInvitationsNavigation = {},
                    onAboutNavigation = onAboutNavigation,
                    currentScreen = stringResource(R.string.invitations),
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
                ChannelInvitationsList(
                    scrollState = scrollState,
                    onBottomScroll = onScrollToBottom,
                    onAcceptInvitation = { onAcceptInvitation(it) },
                    onRejectInvitation = { onRejectInvitation(it) },
                )
            }
        }

        val acceptedInvitationString = stringResource(R.string.invitation_accepted)
        val rejectedInvitationString = stringResource(R.string.invitation_rejected)
        LaunchedEffect(state) {
            when (state) {
                is InvitationsScreenState.InvitationsListError -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = state.problem.detail),
                    )
                }
                is InvitationsScreenState.AcceptedInvitation -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = acceptedInvitationString),
                    )
                }
                is InvitationsScreenState.RejectedInvitation -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = rejectedInvitationString),
                    )
                }
                else -> {}
            }
        }
    }
}
