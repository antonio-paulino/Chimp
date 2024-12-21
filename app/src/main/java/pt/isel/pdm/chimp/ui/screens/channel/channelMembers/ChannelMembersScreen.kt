package pt.isel.pdm.chimp.ui.screens.channel.channelMembers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.buttons.BackButton
import pt.isel.pdm.chimp.ui.components.channel.members.ChannelMembersList
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun ChannelMembersScreen(
    channel: Channel?,
    state: ChannelMembersScreenState,
    onRemoveMember: (channel: Channel, member: ChannelMember) -> Unit,
    onUpdateMemberRole: (channel: Channel, member: ChannelMember, role: ChannelRole) -> Unit,
    onBack: () -> Unit,
    user: User?,
) {
    if (user == null || channel == null) {
        onBack()
        return
    }
    ChIMPTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopBar(
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BackButton(onBack)
                            Text("${channel.name.value} ${stringResource(id = R.string.members)}")
                        }
                    },
                )
            },
            containerColor = Color.Transparent,
        ) { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                ChannelMembersList(
                    channel = channel,
                    onRemoveMember = onRemoveMember,
                    onUpdateMemberRole = onUpdateMemberRole,
                    user = user,
                )
            }
        }

        val memberRemovedMessage = stringResource(id = R.string.member_removed)
        val roleUpdatedMessage = stringResource(id = R.string.role_updated)
        LaunchedEffect(state) {
            when (state) {
                is ChannelMembersScreenState.ChannelMembersListError -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = state.problem.detail),
                    )
                }
                is ChannelMembersScreenState.MemberRemoved -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = memberRemovedMessage + " ${state.member.name}"),
                    )
                }
                is ChannelMembersScreenState.MemberRoleChanged -> {
                    snackBarHostState.showSnackbar(
                        SnackBarVisuals(message = "${state.member.name} $roleUpdatedMessage to ${state.member.role}"),
                    )
                }
                else -> {}
            }
        }
    }
}
