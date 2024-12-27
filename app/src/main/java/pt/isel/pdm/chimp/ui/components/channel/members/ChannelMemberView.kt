package pt.isel.pdm.chimp.ui.components.channel.members

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.components.buttons.DeleteButton
import pt.isel.pdm.chimp.ui.components.channel.ChannelMemberRoleIcon
import pt.isel.pdm.chimp.ui.components.inputs.RoleInput

@Composable
fun ChannelMemberView(
    channel: Channel,
    member: ChannelMember,
    onRemoveMember: () -> Unit,
    onUpdateMemberRole: (ChannelRole) -> Unit,
    user: User,
) {
    ChannelMemberContainer {
        ChannelMemberNameView(member = member)
        if (channel.isOwner(user) && member.id != user.id) {
            ChannelMemberActions(
                member = member,
                onRemoveMember = onRemoveMember,
                onUpdateMemberRole = onUpdateMemberRole,
            )
        } else {
            Spacer(modifier = Modifier.width(8.dp))
            ChannelMemberRoleIcon(channel = channel, user = member)
        }
    }
}

@Composable
fun ChannelMemberNameView(member: ChannelMember) {
    Text(
        text = member.name.value,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(end = 16.dp),
    )
}

@Composable
fun ChannelMemberActions(
    member: ChannelMember,
    onRemoveMember: () -> Unit,
    onUpdateMemberRole: (ChannelRole) -> Unit,
) {
    val loading = remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RoleInput(
            modifier = Modifier.weight(0.9f).padding(start = 8.dp, end = 8.dp),
            role = member.role,
            enabled = !loading.value,
            onRoleChange = {
                onUpdateMemberRole(it)
            },
        )
        if (loading.value) {
            LoadingSpinner()
        } else {
            DeleteButton(
                onClick = {
                    loading.value = true
                    onRemoveMember()
                },
            )
        }
    }
}

@Composable
fun ChannelMemberContainer(
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        content()
    }
}
