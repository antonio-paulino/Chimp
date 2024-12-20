package pt.isel.pdm.chimp.ui.components.invitations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.components.buttons.AcceptButton
import pt.isel.pdm.chimp.ui.components.buttons.RejectButton
import java.time.temporal.ChronoUnit

@Composable
fun ChannelInvitationView(
    invitation: ChannelInvitation,
    onAcceptInvitation: (ChannelInvitation) -> Unit,
    onRejectInvitation: (ChannelInvitation) -> Unit,
) {
    val isLoading = remember { mutableStateOf(false) }
    ChannelInvitationContainer {
        InvitationHeader(invitation = invitation)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isLoading.value) {
                LoadingSpinner()
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                InvitationActions(
                    invitation = invitation,
                    onAcceptInvitation = { invitation ->
                        isLoading.value = true
                        onAcceptInvitation(invitation)
                    },
                    onRejectInvitation = { invitation ->
                        isLoading.value = true
                        onRejectInvitation(invitation)
                    },
                )
            }
        }
    }
}

@Composable
fun ChannelInvitationContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.20f), MaterialTheme.shapes.medium)
                .padding(16.dp)
                .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
fun InvitationActions(
    invitation: ChannelInvitation,
    onAcceptInvitation: (ChannelInvitation) -> Unit,
    onRejectInvitation: (ChannelInvitation) -> Unit,
    enabled: Boolean = true,
) {
    AcceptButton(onClick = { onAcceptInvitation(invitation) }, enabled = enabled)
    RejectButton(onClick = { onRejectInvitation(invitation) }, enabled = enabled)
}

@Composable
fun InvitationHeader(
    invitation: ChannelInvitation,
    modifier: Modifier = Modifier,
) {
    Row {
        Column(
            modifier = modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = invitation.channel.name.value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = invitation.inviter.name.value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${invitation.expiresAt.toLocalDate()} - ${invitation.expiresAt.truncatedTo(ChronoUnit.MINUTES).toLocalTime()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
