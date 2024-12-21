package pt.isel.pdm.chimp.ui.screens.channel.channelInvitations

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManager
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface ChannelInvitationsScreenState {
    data object ChannelInvitationsList : ChannelInvitationsScreenState

    data object Loading : ChannelInvitationsScreenState

    data class InvitationRemoved(val invitation: ChannelInvitation) : ChannelInvitationsScreenState

    data class InvitationRoleChanged(val invitation: ChannelInvitation) : ChannelInvitationsScreenState

    data class ChannelInvitationsListError(val problem: Problem) : ChannelInvitationsScreenState
}

class ChannelInvitationsViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    private val entityReferenceManager: EntityReferenceManager,
    initialScreenState: ChannelInvitationsScreenState = ChannelInvitationsScreenState.ChannelInvitationsList,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelInvitationsScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelInvitationsScreenState> = _state

    fun deleteInvitation(invitation: ChannelInvitation) {
        _state.value = ChannelInvitationsScreenState.Loading
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelInvitationsScreenState.ChannelInvitationsListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.invitationService.deleteInvitation(
                    session = session,
                    invitation = invitation,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelInvitationsScreenState.ChannelInvitationsListError(it)) },
            onSuccess = { _state.emit(ChannelInvitationsScreenState.InvitationRemoved(invitation)) },
        )
    }

    fun updateInvitationRole(
        invitation: ChannelInvitation,
        role: ChannelRole,
    ) {
        if (role == invitation.role) return
        _state.value = ChannelInvitationsScreenState.Loading
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelInvitationsScreenState.ChannelInvitationsListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.invitationService.updateInvitation(
                    session = session,
                    invitation = invitation,
                    role = role,
                    expiresAt = null,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelInvitationsScreenState.ChannelInvitationsListError(it)) },
            onSuccess = { _state.emit(ChannelInvitationsScreenState.InvitationRoleChanged(invitation.copy(role = role))) },
        )
    }

    suspend fun fetchInvitations(
        paginationRequest: PaginationRequest,
        currentItems: List<ChannelInvitation>,
    ): Either<Problem, Pagination<ChannelInvitation>> {
        var result: Either<Problem, Pagination<ChannelInvitation>> = failure(Problem.UnexpectedProblem)
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                result = failure(Problem.NoConnection)
                null
            },
            request = { session ->
                services.invitationService.getChannelInvitations(
                    channel = entityReferenceManager.channel.firstOrNull() ?: return@launchRequestRefreshing failure(Problem.UnexpectedProblem),
                    session = session,
                    pagination = paginationRequest,
                    sort = null,
                    after = currentItems.lastOrNull()?.id,
                )
            },
            refresh = services.authService::refresh,
            onError = {
                result = failure(it)
            },
            onSuccess = {
                result = it
            },
        ).join()
        return result
    }
}
