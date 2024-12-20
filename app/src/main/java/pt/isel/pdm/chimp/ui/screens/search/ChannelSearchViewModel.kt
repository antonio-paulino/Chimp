package pt.isel.pdm.chimp.ui.screens.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface ChannelSearchListScreenState {
    data object ChannelSearchList : ChannelSearchListScreenState

    data object Loading : ChannelSearchListScreenState

    data class ChannelSearchListError(val problem: Problem) : ChannelSearchListScreenState

    data class JoinedChannel(val channel: Channel) : ChannelSearchListScreenState
}

class DebouncedFlow<T>(
    initialValue: T,
    private val debounceTime: Long,
) {
    private val _debouncedState = MutableStateFlow(initialValue)

    @OptIn(FlowPreview::class)
    val debouncedState: Flow<T>
        get() = _debouncedState.debounce(debounceTime)

    fun setValue(value: T) {
        _debouncedState.value = value
    }
}

open class ChannelSearchViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    initialScreenState: ChannelSearchListScreenState = ChannelSearchListScreenState.ChannelSearchList,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelSearchListScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelSearchListScreenState> = _state

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

    fun joinChannel(
        channel: Channel,
        onJoin: suspend() -> Unit,
    ) {
        _state.value = ChannelSearchListScreenState.Loading
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelSearchListScreenState.ChannelSearchListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.channelService.joinChannel(
                    session = session,
                    channel = channel,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelSearchListScreenState.ChannelSearchListError(it)) },
            onSuccess = {
                _state.emit(ChannelSearchListScreenState.JoinedChannel(channel))
                onJoin()
            },
        )
    }

    suspend fun fetchChannelsByName(
        paginationRequest: PaginationRequest,
        currentItems: List<Channel>,
    ): Either<Problem, Pagination<Channel>> {
        var result: Either<Problem, Pagination<Channel>> = failure(Problem.UnexpectedProblem)
        val job =
            launchRequestRefreshing(
                sessionManager = sessionManager,
                noConnectionRequest = {
                    _state.emit(ChannelSearchListScreenState.ChannelSearchListError(Problem.NoConnection))
                    null
                },
                request = { session ->
                    services.channelService.getChannels(
                        name = searchQuery.firstOrNull(),
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
