package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole

@Composable
fun ChannelRoleIcon(channel: Channel) {
    when (channel.defaultRole) {
        ChannelRole.MEMBER -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.member_role),
            )
        }
        ChannelRole.GUEST -> {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.guest_role),
            )
        }
        else -> {
            throw IllegalStateException("Illegal channel role as default: ${channel.defaultRole}")
        }
    }
}
