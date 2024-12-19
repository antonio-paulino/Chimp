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

sealed interface ChannelScreenState {
    data object ChannelsList : ChannelScreenState

    data class ChannelsListError(val problem: Problem) : ChannelScreenState
}

open class ChannelsViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    private val storage: Storage,
    initialScreenState: ChannelScreenState = ChannelScreenState.ChannelsList,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelScreenState> = _state

    fun logout() {
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelScreenState.ChannelsListError(Problem.NoConnection)
                null
            },
            request = services.authService::logout,
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelScreenState.ChannelsListError(it)) },
            onSuccess = { sessionManager.clear() },
        )
    }

    suspend fun fetchChannels(
        paginationRequest: PaginationRequest,
        currentItems: List<Channel>,
    ): Either<Problem, Pagination<Channel>> {
        var result: Either<Problem, Pagination<Channel>> = failure(Problem.UnexpectedProblem)
        val job =
            launchRequestRefreshing(
                sessionManager = sessionManager,
                noConnectionRequest = {
                    storage.channelRepository.getChannels(
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
            )
        job.join()
        return result
    }
}
