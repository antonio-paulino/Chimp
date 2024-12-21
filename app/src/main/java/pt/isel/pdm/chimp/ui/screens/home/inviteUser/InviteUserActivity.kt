package pt.isel.pdm.chimp.ui.screens.home.inviteUser

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.ui.DependenciesActivity
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationOptions
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class InviteUserActivity : DependenciesActivity() {
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
                val session by dependencies.sessionManager.session.collectAsState(
                    initial = runBlocking { dependencies.sessionManager.session.firstOrNull() },
                )
                CreateUserInviteScreen(
                    state = state,
                    session = session,
                    onCreateInvite = inviteUserViewModel::createInvite,
                    onBack = { finish() },
                )
            }
        }
    }
}
