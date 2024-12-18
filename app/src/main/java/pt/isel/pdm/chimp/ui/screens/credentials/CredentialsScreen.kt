package pt.isel.pdm.chimp.ui.screens.credentials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.screens.credentials.views.LoginView
import pt.isel.pdm.chimp.ui.screens.credentials.views.RegisterView
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals
import pt.isel.pdm.chimp.ui.utils.getMessage

@Composable
fun CredentialsScreen(
    state: CredentialsScreenState,
    onSuccessfulAuthentication: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    doLogin: (String, String) -> Unit,
    doRegister: (String, String, String, String) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (state) {
                    is CredentialsScreenState.Login, is CredentialsScreenState.LoginError -> {
                        state as CredentialsScreenState.Login
                        LoginView(
                            emailOrUsernameInitialValue = state.emailOrUsername,
                            passwordInitialValue = state.password,
                            onRegisterClick = onRegisterClick,
                            onLogin = doLogin,
                        )
                    }
                    is CredentialsScreenState.Register, is CredentialsScreenState.RegisterError -> {
                        state as CredentialsScreenState.Register
                        RegisterView(
                            emailInitialValue = state.email,
                            usernameInitialValue = state.username,
                            passwordInitialValue = state.password,
                            onLoginClick = onLoginClick,
                            onRegister = doRegister,
                        )
                    }
                    is CredentialsScreenState.Loading -> LoadingSpinner()
                    is CredentialsScreenState.Success -> onSuccessfulAuthentication()
                }
            }
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is CredentialsScreenState.LoginError -> {
                val message =
                    if (state.problem is Problem.InputValidationProblem) {
                        "Invalid Credentials"
                    } else {
                        state.problem.getMessage()
                    }
                snackBarHostState.showSnackbar(SnackBarVisuals(message, isError = true))
            }
            is CredentialsScreenState.RegisterError -> {
                snackBarHostState.showSnackbar(SnackBarVisuals(state.problem.getMessage(), isError = true))
            }
            else -> {}
        }
    }
}
