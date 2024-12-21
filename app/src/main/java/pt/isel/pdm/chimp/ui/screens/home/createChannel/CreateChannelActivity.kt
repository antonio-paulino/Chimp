package pt.isel.pdm.chimp.ui.screens.home.createChannel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class CreateChannelActivity : ChannelsActivity() {
    val createChannelViewModel by initializeViewModel { dependencies ->
        CreateChannelViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChIMPTheme {
                val state by createChannelViewModel.state.collectAsState(initial = CreateChannelScreenState.CreatingChannel())
                val session by dependencies.sessionManager.session.collectAsState(
                    initial =
                        runBlocking {
                            dependencies.sessionManager.session.firstOrNull()
                        },
                )
                CreateChannelScreen(
                    state = state,
                    session = session,
                    onCreateChannel = createChannelViewModel::createChannel,
                    onBack = { finish() },
                )
            }
        }
    }
}
