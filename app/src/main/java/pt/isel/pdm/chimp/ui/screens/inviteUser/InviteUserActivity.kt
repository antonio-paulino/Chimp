package pt.isel.pdm.chimp.ui.screens.inviteUser

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationOptions
import pt.isel.pdm.chimp.ui.screens.ChannelsActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class InviteUserActivity : ChannelsActivity() {
    private val inviteUserViewModel by initializeViewModel { dependencies ->
        InviteUserViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChIMPTheme {
                val state by inviteUserViewModel.state.collectAsState(
                    initial = InviteUserScreenState.CreatingInvite(ExpirationOptions.THIRTY_MINUTES),
                )
                CreateUserInviteScreen(
                    state = state,
                    onCreateInvite = inviteUserViewModel::createInvite,
                    onBack = { finish() },
                )
            }
        }
    }
}
