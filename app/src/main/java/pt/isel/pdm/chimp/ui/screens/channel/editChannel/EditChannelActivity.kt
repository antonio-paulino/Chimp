package pt.isel.pdm.chimp.ui.screens.channel.editChannel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.ui.DependenciesActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class EditChannelActivity : DependenciesActivity() {
    private val editChannelViewModel by initializeViewModel { dependencies ->
        EditChannelViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChIMPTheme {
                val state by editChannelViewModel.state.collectAsState(initial = EditChannelScreenState.EditingChannel)
                val channel by dependencies.entityReferenceManager.channel.collectAsState(
                    initial =
                        runBlocking {
                            dependencies.entityReferenceManager.channel.firstOrNull()
                        },
                )
                val session by dependencies.sessionManager.session.collectAsState(
                    initial =
                        runBlocking {
                            dependencies.sessionManager.session.firstOrNull()
                        },
                )
                EditChannelScreen(
                    channel = channel,
                    session = session,
                    state = state,
                    onEditChannel = editChannelViewModel::updateChannel,
                    onBack = { finish() },
                )
            }
        }
    }
}
