package pt.isel.pdm.chimp.ui.components.channel.searchResults

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.components.channel.ChannelContainer
import pt.isel.pdm.chimp.ui.components.channel.ChannelMembersView
import pt.isel.pdm.chimp.ui.components.channel.ChannelNameView
import pt.isel.pdm.chimp.ui.components.channel.ChannelPrivacyIcon
import pt.isel.pdm.chimp.ui.components.channel.ChannelRoleIcon

@Composable
fun ChannelResultView(
    channel: Channel,
    onJoinChannel: () -> Unit,
) {
    val isLoading = remember { mutableStateOf(false) }
    ChannelContainer {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChannelNameView(channel = channel)
            ChannelPrivacyIcon(channel = channel)
        }
        ChannelMembersView(channel = channel)
        ChannelRoleIcon(channel = channel)
        if (isLoading.value) {
            LoadingSpinner()
        } else {
            JoinChannelButton(onClick = {
                isLoading.value = true
                onJoinChannel()
            })
        }
    }
}

@Composable
fun JoinChannelButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.join_channel),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
    )
}
