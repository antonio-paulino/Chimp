package pt.isel.pdm.chimp.ui.components.invitations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun ChannelInvitationsList(
    scrollState: InfiniteScrollState<ChannelInvitation>,
    onBottomScroll: () -> Unit,
    onAcceptInvitation: (ChannelInvitation) -> Unit,
    onRejectInvitation: (ChannelInvitation) -> Unit,
) {
    InfiniteScroll(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(16.dp),
        reverse = false,
    ) { invitation ->
        ChannelInvitationView(
            invitation = invitation,
            onAcceptInvitation = { onAcceptInvitation(invitation) },
            onRejectInvitation = { onRejectInvitation(invitation) },
        )
    }
}