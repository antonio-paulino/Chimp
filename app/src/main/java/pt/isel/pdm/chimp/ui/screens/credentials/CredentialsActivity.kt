package pt.isel.pdm.chimp.ui.screens.credentials

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.screens.ChannelsActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class CredentialsActivity : ChannelsActivity() {
    private val viewModel by initializeViewModel { dependencies ->
        CredentialsViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "CredentialsActivity.onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "CredentialsActivity.onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "CredentialsActivity.onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "CredentialsActivity.onStop")
    }
}
