package pt.isel.pdm.chimp.ui.screens.channel.createInvitation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManager
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationOptions
import pt.isel.pdm.chimp.ui.screens.search.DebouncedFlow
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface CreateChannelInvitationScreenState {
    val role: ChannelRole
    val expiration: ExpirationOptions

    data class SearchingUsers(
        override val role: ChannelRole = ChannelRole.MEMBER,
        override val expiration: ExpirationOptions = ExpirationOptions.ONE_DAY,
    ) : CreateChannelInvitationScreenState

    data class SearchingUsersError(
        val problem: Problem,
        override val role: ChannelRole = ChannelRole.MEMBER,
        override val expiration: ExpirationOptions = ExpirationOptions.ONE_DAY,
    ) : CreateChannelInvitationScreenState

    data class SubmittingInvitation(
        override val role: ChannelRole = ChannelRole.MEMBER,
        override val expiration: ExpirationOptions = ExpirationOptions.ONE_DAY,
    ) : CreateChannelInvitationScreenState

    data class SubmittingInvitationError(
        val problem: Problem,
        val user: User,
        override val role: ChannelRole = ChannelRole.MEMBER,
        override val expiration: ExpirationOptions = ExpirationOptions.ONE_DAY,
    ) : CreateChannelInvitationScreenState

    data class InvitationSent(
        val user: User,
        override val role: ChannelRole,
        override val expiration: ExpirationOptions,
    ) : CreateChannelInvitationScreenState
}

class CreateChannelInvitationViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    private val entityReferenceManager: EntityReferenceManager,
    initialScreenState: CreateChannelInvitationScreenState = CreateChannelInvitationScreenState.SearchingUsers(),
) : ViewModel() {
    private val _state: MutableStateFlow<CreateChannelInvitationScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<CreateChannelInvitationScreenState> = _state

    private val _searchQuery =
        DebouncedFlow(
            initialValue = "",
            debounceTime = 200,
        )

    val searchQuery: Flow<String>
        get() = _searchQuery.debouncedState

    fun setSearchQuery(query: String) {
        _searchQuery.setValue(query)
    }

    fun submitInvitation(
        user: User,
        role: ChannelRole,
        expiration: ExpirationOptions,
    ) {
        _state.value = CreateChannelInvitationScreenState.SubmittingInvitation(role, expiration)
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = CreateChannelInvitationScreenState.SubmittingInvitationError(Problem.NoConnection, user, role, expiration)
                null
            },
            request = { session ->
                services.invitationService.createChannelInvitation(
                    channel =
                        entityReferenceManager.channel.firstOrNull()
                            ?: return@launchRequestRefreshing failure(Problem.UnexpectedProblem),
                    session = session,
                    role = role,
                    invitee = user,
                    expiresAt = expiration.expirationDate(),
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(CreateChannelInvitationScreenState.SubmittingInvitationError(it, user, role, expiration)) },
            onSuccess = { _state.emit(CreateChannelInvitationScreenState.InvitationSent(user, role, expiration)) },
        )
    }

    suspend fun fetchUsers(
        paginationRequest: PaginationRequest,
        currentItems: List<User>,
    ): Either<Problem, Pagination<User>> {
        var result: Either<Problem, Pagination<User>> = failure(Problem.UnexpectedProblem)
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                result = failure(Problem.NoConnection)
                null
            },
            request = { session ->
                services.userService.getUsers(
                    name = searchQuery.firstOrNull(),
                    session = session,
                    pagination = paginationRequest,
                    sort = null,
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
