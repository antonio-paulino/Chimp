package pt.isel.pdm.chimp.ui.screens.credentials

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pt.isel.pdm.chimp.ui.DependenciesActivity
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class CredentialsActivity : DependenciesActivity() {
    private val viewModel by initializeViewModel { dependencies ->
        CredentialsViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChIMPTheme {
                val state by viewModel.state.collectAsState(initial = CredentialsScreenState.Login())
                CredentialsScreen(
                    state = state,
                    onSuccessfulAuthentication = {
                        navigateTo(ChannelsActivity::class.java)
                        finish()
                    },
                    onLoginClick = viewModel::onLoginClick,
                    onRegisterClick = viewModel::onRegisterClick,
                    doLogin = viewModel::doLogin,
                    doRegister = viewModel::doRegister,
                )
            }
        }
    }
}
