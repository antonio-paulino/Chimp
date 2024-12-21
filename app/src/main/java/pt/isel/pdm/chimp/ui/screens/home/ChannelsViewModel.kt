package pt.isel.pdm.chimp.ui.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.infrastructure.storage.Storage
import pt.isel.pdm.chimp.ui.utils.isNetworkAvailable
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface ChannelsScreenState {
    data object ChannelsList : ChannelsScreenState

    data object Loading : ChannelsScreenState

    data class ChannelsListError(val problem: Problem) : ChannelsScreenState
}

open class ChannelsViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    private val storage: Storage,
    initialScreenState: ChannelsScreenState = ChannelsScreenState.ChannelsList,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelsScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelsScreenState> = _state

    fun logout() {
        if (_state.value == ChannelsScreenState.Loading) return
        _state.value = ChannelsScreenState.Loading
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelsScreenState.ChannelsListError(Problem.NoConnection)
                null
            },
            request = services.authService::logout,
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelsScreenState.ChannelsListError(it)) },
            onSuccess = { sessionManager.clear() },
        )
    }

    suspend fun fetchChannels(
        paginationRequest: PaginationRequest,
        currentItems: List<Channel>,
    ): Either<Problem, Pagination<Channel>> {
        var result: Either<Problem, Pagination<Channel>> = failure(Problem.UnexpectedProblem)
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = { session ->
                storage.channelRepository.getChannels(
                    user = session.user,
                    limit = paginationRequest.limit,
                    getCount = false,
                    after = currentItems.lastOrNull()?.id,
                    filterOwned = false,
                )
            },
            request = { session ->
                services.userService.getUserChannels(
                    session = session,
                    pagination = paginationRequest,
                    sort = null,
                    after = currentItems.lastOrNull()?.id,
                    filterOwned = false,
                )
            },
            refresh = services.authService::refresh,
            onError = {
                result = failure(it)
            },
            onSuccess = {
                result = it
                if (ChimpApplication.applicationContext().isNetworkAvailable()) {
                    storage.channelRepository.updateChannels(it.value.items)
                }
            },
        ).join()
        return result
    }
}
