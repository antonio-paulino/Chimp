package pt.isel.pdm.chimp.domain.channel

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R

/**
 * Represents the role of a user in a channel.
 *  - OWNER: can manage the channel and its members
 *  - MEMBER: can read and write messages
 *  - GUEST: can only read messages
 */
enum class ChannelRole {
    OWNER,
    MEMBER, // read-write
    GUEST, // read-only
    ;

    @Composable
    fun toStringResourceRepresentation(): String {
        return when (this) {
            OWNER -> stringResource(R.string.owner)
            MEMBER -> stringResource(R.string.member)
            GUEST -> stringResource(R.string.guest)
        }
    }
}
