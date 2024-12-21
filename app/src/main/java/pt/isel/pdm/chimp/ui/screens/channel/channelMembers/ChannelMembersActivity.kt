package pt.isel.pdm.chimp.ui.screens.channel.channelMembers

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.ui.DependenciesActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class ChannelMembersActivity : DependenciesActivity() {
    private val channelMembersViewModel by initializeViewModel { dependencies ->
        ChannelMembersViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dependencies = application as DependenciesContainer
        setContent {
            val viewModelState by channelMembersViewModel.state.collectAsState(initial = ChannelMembersScreenState.ChannelMembersList)
            val session by dependencies.sessionManager.session.collectAsState(
                initial =
                    runBlocking {
                        dependencies.sessionManager.session.firstOrNull()
                    },
            )
            val channel =
                dependencies.entityReferenceManager.channel.collectAsState(
                    initial =
                        runBlocking {
                            dependencies.entityReferenceManager.channel.firstOrNull()
                        },
                ).value
            ChIMPTheme {
                ChannelMembersScreen(
                    state = viewModelState,
                    onRemoveMember = { channel, member -> channelMembersViewModel.removeMember(channel, member) },
                    onUpdateMemberRole = { channel, member, role -> channelMembersViewModel.updateMemberRole(channel, member, role) },
                    onBack = { finish() },
                    channel = channel,
                    user = session?.user,
                )
            }
        }
    }
}
