package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel

@Composable
fun ChannelPrivacyIcon(
    channel: Channel,
    modifier: Modifier = Modifier,
) {
    if (!channel.isPublic) {
        Icon(
            tint = MaterialTheme.colorScheme.onBackground,
            imageVector = Icons.Default.Lock,
            contentDescription = stringResource(R.string.private_channel),
        )
    } else {
        Box(
            modifier =
                Modifier
                    .size(24.dp)
                    .background(Color.Transparent),
        )
    }
}
