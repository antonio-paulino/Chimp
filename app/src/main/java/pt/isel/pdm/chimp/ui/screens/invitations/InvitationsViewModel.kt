package pt.isel.pdm.chimp.ui.screens.invitations

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface InvitationsScreenState {
    data object InvitationsList : InvitationsScreenState

    data object AcceptedInvitation : InvitationsScreenState

    data object RejectedInvitation : InvitationsScreenState

    data class InvitationsListError(val problem: Problem) : InvitationsScreenState
}

open class InvitationsViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    initialScreenState: InvitationsScreenState = InvitationsScreenState.InvitationsList,
) : ViewModel() {
    private val _state: MutableStateFlow<InvitationsScreenState> = MutableStateFlow(initialScreenState)

    val state = _state

    fun acceptInvitation(invitation: ChannelInvitation) {
        state.value = InvitationsScreenState.InvitationsList
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = InvitationsScreenState.InvitationsListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.invitationService.acceptOrRejectInvitation(
                    session = session,
                    channel = invitation.channel,
                    invitation = invitation,
                    accept = true,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(InvitationsScreenState.InvitationsListError(it)) },
            onSuccess = { _state.emit(InvitationsScreenState.AcceptedInvitation) },
        )
    }

    fun rejectInvitation(invitation: ChannelInvitation) {
        state.value = InvitationsScreenState.InvitationsList
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = InvitationsScreenState.InvitationsListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.invitationService.acceptOrRejectInvitation(
                    session = session,
                    channel = invitation.channel,
                    invitation = invitation,
                    accept = false,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(InvitationsScreenState.InvitationsListError(it)) },
            onSuccess = { _state.emit(InvitationsScreenState.RejectedInvitation) },
        )
    }

    suspend fun fetchInvitations(
        paginationRequest: PaginationRequest,
        currentItems: List<ChannelInvitation>,
    ): Either<Problem, Pagination<ChannelInvitation>> {
        var result: Either<Problem, Pagination<ChannelInvitation>> = failure(Problem.UnexpectedProblem)
        val job = launchRequestRefreshing(
                sessionManager = sessionManager,
                noConnectionRequest = {
                    _state.emit(InvitationsScreenState.InvitationsListError(Problem.NoConnection))
                    null
                },
                request = { session ->
                    services.invitationService.getUserInvitations(
                        session = session,
                        pagination = paginationRequest,
                        sort = null,
                        after = currentItems.lastOrNull()?.id,
                    )
                },
                refresh = services.authService::refresh,
                onError = { result = failure(it) },
                onSuccess = { result = it },
            )
        job.join()
        return result
    }
}