package pt.isel.pdm.chimp.ui.components.invitations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun ChannelInvitationsManagedList(
    scrollState: InfiniteScrollState<ChannelInvitation>,
    onBottomScroll: () -> Unit,
    onInvitationDeleted: (ChannelInvitation) -> Unit,
    onInvitationRoleChanged: (ChannelInvitation, ChannelRole) -> Unit,
) {
    InfiniteScroll(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(16.dp),
        itemSpacing = PaddingValues(top = 16.dp),
        reverse = false,
    ) { invitation ->
        ChannelInvitationManagedView(
            invitation = invitation,
            onInvitationDeleted = { onInvitationDeleted(invitation) },
            onInvitationRoleChanged = { role ->
                onInvitationRoleChanged(invitation, role)
            },
        )
    }
}
