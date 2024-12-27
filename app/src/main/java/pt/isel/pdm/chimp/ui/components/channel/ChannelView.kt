package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User

@Composable
fun ChannelView(
    channel: Channel,
    onChannelSelected: (Channel) -> Unit,
    user: User,
) {
    ChannelContainer(
        modifier = Modifier.clickable { onChannelSelected(channel) },
    ) {
        ChannelMemberRoleIcon(channel = channel, user = channel.members.find { it.id == user.id }!!)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChannelNameView(channel = channel)
            ChannelPrivacyIcon(channel = channel)
        }
        ChannelMembersView(channel = channel)
        ChannelRoleIcon(channel = channel)
    }
}

@Composable
fun ChannelMemberRoleIcon(
    channel: Channel,
    user: ChannelMember,
) {
    when (channel.members.find { it.id == user.id }!!.role) {
        ChannelRole.OWNER -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.Default.Build,
                contentDescription = stringResource(R.string.guest_role),
            )
        }
        ChannelRole.MEMBER -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.member_role),
            )
        }
        ChannelRole.GUEST -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(R.string.guest_role),
            )
        }
    }
}

@Composable
fun ChannelNameView(channel: Channel) {
    Text(
        text = channel.name.value,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun ChannelMembersView(channel: Channel) {
    Text(
        text = "  ${channel.members.size} ${stringResource(id = R.string.members)}",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
}

@Composable
fun ChannelContainer(
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
