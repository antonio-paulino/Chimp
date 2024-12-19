package pt.isel.pdm.chimp.ui.screens.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.wrappers.email.toEmail
import pt.isel.pdm.chimp.domain.wrappers.name.toName
import pt.isel.pdm.chimp.domain.wrappers.password.toPassword
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequest
import java.time.LocalDateTime
import java.util.UUID

sealed interface CredentialsScreenState {
    data class Login(val emailOrUsername: String = "", val password: String = "") : CredentialsScreenState

    data class LoginError(val emailOrUsername: String = "", val password: String = "", val problem: Problem) : CredentialsScreenState

    data class Register(
        val email: String = "",
        val username: String = "",
        val password: String = "",
        val token: String = "",
    ) : CredentialsScreenState

    data class RegisterError(
        val email: String = "",
        val username: String = "",
        val password: String = "",
        val token: String = "",
        val problem: Problem,
    ) : CredentialsScreenState

    data class Loading(val message: String = "") : CredentialsScreenState

    data object Success : CredentialsScreenState
}

class CredentialsViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _state: MutableStateFlow<CredentialsScreenState> = MutableStateFlow(CredentialsScreenState.Login())

    val state: Flow<CredentialsScreenState> = _state

    fun doLogin(
        emailOrUsername: String,
        password: String,
    ) {
        launchRequest(
            noConnectionRequest = {
                _state.emit(CredentialsScreenState.LoginError(emailOrUsername, password, Problem.NoConnection))
                null
            },
            request = {
                _state.emit(CredentialsScreenState.Loading("Logging in..."))
                if ('@' in emailOrUsername) {
                    services.authService.login(null, emailOrUsername, password)
                } else {
                    services.authService.login(emailOrUsername, null, password)
                }
            },
            onError = { problem ->
                _state.emit(CredentialsScreenState.LoginError(emailOrUsername, password, problem))
            },
            onSuccess = { session ->
                sessionManager.set(session.value)
                _state.emit(CredentialsScreenState.Success)
            },
        )
    }

    fun doRegister(
        email: String,
        username: String,
        password: String,
        token: String,
    ) {
        var registerSuccess = false
        val job =
            launchRequest(
                noConnectionRequest = {
                    _state.emit(CredentialsScreenState.RegisterError(email, username, password, token, Problem.NoConnection))
                    null
                },
                request = {
                    _state.emit(CredentialsScreenState.Loading("Registering..."))
                    services.authService.register(
                        username.toName(),
                        email.toEmail(),
                        password.toPassword(),
                        token = ImInvitation(token = UUID.fromString(token), expiresAt = LocalDateTime.now()),
                    )
                },
                onError = { problem ->
                    _state.emit(CredentialsScreenState.RegisterError(email, username, password, token, problem))
                },
                onSuccess = { registerSuccess = true },
            )
        job.invokeOnCompletion {
            if (registerSuccess) {
                doLogin(email, password)
            }
        }
    }

    fun onRegisterClick() {
        this.viewModelScope.launch {
            _state.emit(CredentialsScreenState.Register())
        }
    }

    fun onLoginClick() {
        this.viewModelScope.launch {
            _state.emit(CredentialsScreenState.Login())
        }
    }
}
