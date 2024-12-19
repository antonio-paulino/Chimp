package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel

@Composable
fun ChannelResultView(
    channel: Channel,
    onJoinChannel: () -> Unit,
) {
    ChannelContainer {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChannelNameView(channel = channel)
            ChannelPrivacyIcon(channel = channel)
        }
        ChannelMembersView(channel = channel)
        ChannelRoleIcon(channel = channel)
        IconButton(onClick = onJoinChannel) {
            Icon(
                tint = MaterialTheme.colorScheme.primary,
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.join_channel),
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}