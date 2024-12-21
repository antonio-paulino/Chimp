package pt.isel.pdm.chimp.ui.components.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.components.buttons.InviteButton
import pt.isel.pdm.chimp.ui.components.scroll.InfiniteScroll
import pt.isel.pdm.chimp.ui.screens.channel.createInvitation.CreateChannelInvitationScreenState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState

@Composable
fun UserResultsList(
    channel: Channel,
    user: User,
    state: CreateChannelInvitationScreenState,
    scrollState: InfiniteScrollState<User>,
    onBottomScroll: () -> Unit,
    onUserInvite: (User) -> Unit,
) {
    InfiniteScroll(
        scrollState = scrollState,
        onBottomScroll = onBottomScroll,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        filterCondition = { channel.getMemberRole(it) == null && it != user },
    ) {
        UserView(
            user = it,
            actions = {
                UserInviteActions(
                    user = it,
                    state = state,
                    onUserInvite = onUserInvite,
                )
            },
        )
    }
}

@Composable
fun UserView(
    user: User,
    actions: @Composable () -> Unit,
) {
    UserContainer {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            UserName(user = user)
            actions()
        }
    }
}

@Composable
fun UserName(user: User) {
    Text(
        text = user.name.value,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
fun UserContainer(
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

@Composable
fun UserInviteActions(
    user: User,
    state: CreateChannelInvitationScreenState,
    onUserInvite: (User) -> Unit,
) {
    val (invited, setInvited) = remember { mutableStateOf(false) }
    val (loading, setLoading) = remember { mutableStateOf(false) }

    when {
        !invited && !loading ->
            InviteButton(
                onClick = {
                    setLoading(true)
                    onUserInvite(user)
                },
            )
        loading -> LoadingSpinner()
    }

    LaunchedEffect(state) {
        when (state) {
            is CreateChannelInvitationScreenState.InvitationSent -> {
                if (state.user == user) {
                    setInvited(true)
                    setLoading(false)
                }
            }
            is CreateChannelInvitationScreenState.SubmittingInvitationError -> {
                if (state.user == user) {
                    setLoading(false)
                    if (state.problem.status == 400 || state.problem.status == 404) {
                        setInvited(true)
                    }
                }
            }
            else -> {}
        }
    }
}
