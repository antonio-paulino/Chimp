package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun ChannelView(
    channel: Channel,
    onChannelSelected: (Channel) -> Unit,
) {
    ChannelContainer(
        modifier = Modifier.clickable { onChannelSelected(channel) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChannelNameView(channel = channel)
            ChannelPrivacyIcon(channel = channel)
        }
        ChannelMembersView(channel = channel)
        ChannelRoleIcon(channel = channel)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ChannelNameView(
    channel: Channel,
) {
    Text(
        text = channel.name.value,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun ChannelMembersView(
    channel: Channel,
) {
    Text(
        text = "  ${channel.members.size} ${stringResource(id = R.string.members)}",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
}

@Composable
fun ChannelContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.20f), MaterialTheme.shapes.medium)
            .padding(16.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()
    }
}