package pt.isel.pdm.chimp.ui.screens.inviteUser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationOptions
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface InviteUserScreenState {
    data class CreatingInvite(val expirationDate: ExpirationOptions) : InviteUserScreenState

    data class InviteCreationError(val expirationDate: ExpirationOptions, val problem: Problem) : InviteUserScreenState

    data class InviteCreated(val expirationDate: ExpirationOptions, val invite: ImInvitation) : InviteUserScreenState
}

class InviteUserViewModel internal constructor(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    initialScreenState: InviteUserScreenState =
        InviteUserScreenState.CreatingInvite(
            ExpirationOptions.THIRTY_MINUTES,
        ),
) : ViewModel() {
    private val _state: MutableStateFlow<InviteUserScreenState> = MutableStateFlow(initialScreenState)

    val state = _state

    fun createInvite(expirationOptions: ExpirationOptions) {
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = InviteUserScreenState.InviteCreationError(expirationOptions, Problem.NoConnection)
                null
            },
            request = { services.authService.createInvitation(sessionManager.session.first()!!, expirationOptions.expirationDate) },
            refresh = services.authService::refresh,
            onError = { _state.emit(InviteUserScreenState.InviteCreationError(ExpirationOptions.THIRTY_MINUTES, it)) },
            onSuccess = { _state.emit(InviteUserScreenState.InviteCreated(expirationOptions, it.value)) },
        )
    }
}
