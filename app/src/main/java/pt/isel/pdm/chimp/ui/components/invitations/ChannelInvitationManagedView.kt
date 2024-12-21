package pt.isel.pdm.chimp.ui.components.invitations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.components.buttons.DeleteButton
import pt.isel.pdm.chimp.ui.components.inputs.RoleInput

@Composable
fun ChannelInvitationManagedView(
    invitation: ChannelInvitation,
    onInvitationDeleted: (ChannelInvitation) -> Unit,
    onInvitationRoleChanged: (ChannelRole) -> Unit,
) {
    ChannelInvitationContainer {
        InvitationHeader(invitation = invitation, received = false)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InvitationManagedActions(
                invitation = invitation,
                onInvitationDeleted = { invitation ->
                    onInvitationDeleted(invitation)
                },
                onInvitationRoleChanged = { role ->
                    onInvitationRoleChanged(role)
                },
            )
        }
    }
}

@Composable
fun InvitationManagedActions(
    invitation: ChannelInvitation,
    onInvitationDeleted: (ChannelInvitation) -> Unit,
    onInvitationRoleChanged: (ChannelRole) -> Unit,
    enabled: Boolean = true,
) {
    val loading = remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RoleInput(
            modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp),
            role = invitation.role,
            enabled = !loading.value && enabled,
            onRoleChange = {
                onInvitationRoleChanged(it)
            },
        )
        if (loading.value) {
            LoadingSpinner()
        } else {
            DeleteButton(
                onClick = {
                    loading.value = true
                    onInvitationDeleted(invitation)
                },
                enabled = enabled,
            )
        }
    }
}
