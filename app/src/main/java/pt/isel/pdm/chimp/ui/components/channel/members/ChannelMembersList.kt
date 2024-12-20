package pt.isel.pdm.chimp.ui.components.channel.members

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.ui.components.scroll.Scrollable

@Composable
fun ChannelMembersList(
    channel: Channel,
    onRemoveMember: (Channel, ChannelMember) -> Unit,
    onUpdateMemberRole: (Channel, ChannelMember, ChannelRole) -> Unit,
    user: User,
) {
    Scrollable(
        items = channel.members.sortedBy { it.role.ordinal },
        contentPadding = PaddingValues(16.dp),
        itemSpacing = PaddingValues(top = 16.dp),
    ) { member ->
        ChannelMemberView(
            channel = channel,
            member = member,
            onRemoveMember = { onRemoveMember(channel, member) },
            onUpdateMemberRole = { role -> onUpdateMemberRole(channel, member, role) },
            user = user,
        )
    }
}
