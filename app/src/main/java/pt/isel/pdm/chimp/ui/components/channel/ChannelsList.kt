package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun ChannelsList(
    scrollState: InfiniteScrollState<Channel>,
    loadMore: () -> Unit,
    onChannelSelected: (Channel) -> Unit,
) {
    InfiniteScroll(
        scrollState = scrollState,
        loadMore = loadMore,
        reverse = false,
    ) { channel ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    )
                    .padding(16.dp)
                    .clickable { onChannelSelected(channel) },
        ) {
            ChannelPrivacyIcon(channel = channel)
            Row {
                Text(text = channel.name.value, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = "  ${channel.members.size} ${stringResource(id = R.string.members)}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }
            ChannelRoleIcon(channel = channel)
        }
    }
}
