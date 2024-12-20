package pt.isel.pdm.chimp.ui.screens.channel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.infrastructure.services.http.messages.MessageEditedTime
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.infrastructure.storage.Storage
import pt.isel.pdm.chimp.ui.screens.home.ChannelsScreenState
import pt.isel.pdm.chimp.ui.utils.isNetworkAvailable
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface ChannelScreenState {
    data class ChannelMessageCreated(val message: Message) : ChannelScreenState
    data class ChannelMessagesError(val problem: Problem) : ChannelScreenState
    data object MessagesList : ChannelScreenState
    data class EditingMessage(val message: Message) : ChannelScreenState
    data object ChannelMessageUpdated : ChannelScreenState
    data object ChannelMessageDeleted : ChannelScreenState
    data object ChannelDeleted : ChannelScreenState
}

class ChannelViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    private val storage: Storage,
    initialScreenState: ChannelScreenState = ChannelScreenState.MessagesList,
    channel: Channel?
) : ViewModel() {

    private val _channel = mutableStateOf(channel)

    val channel: Channel?
        get() = _channel.value

    fun setChannel(channel: Channel?) {
        _channel.value = channel
    }

    private val _state: MutableStateFlow<ChannelScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelScreenState> = _state


    fun createMessage(channel: Channel, message: String, session: Session) {
        launchRequestRefreshing(

            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelScreenState.ChannelMessagesError(
                    Problem.NoConnection
                )
                null
            },
            request = {
                services.messageService.createMessage(channel, message, session)
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem
                    )
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelMessageCreated(
                        it.value
                    )
                )
            },
            refresh = services.authService::refresh
        )

    }

    fun updateMessage(message: Message, content: String, session: Session) {
        launchRequestRefreshing(

            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelScreenState.ChannelMessagesError(
                    Problem.NoConnection
                )
                null
            },
            request = {
                services.messageService.updateMessage(message, content, session)
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem
                    )
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelMessageUpdated
                )
            },
            refresh = services.authService::refresh
        )

    }

    fun deleteMessage(message: Message, session: Session) {
        viewModelScope.launch {
            launchRequestRefreshing(
                sessionManager = sessionManager,
                noConnectionRequest = {
                    _state.value = ChannelScreenState.ChannelMessagesError(
                        Problem.NoConnection
                    )
                    null
                },
                request = {
                    services.messageService.deleteMessage(message, session)
                },
                onError = { problem ->
                    _state.emit(
                        ChannelScreenState.ChannelMessagesError(
                            problem
                        )
                    )
                },
                onSuccess = {
                    _state.emit(
                        ChannelScreenState.ChannelMessageDeleted
                    )
                },
                refresh = services.authService::refresh
            )
        }
    }

    fun deleteChannel(channel: Channel, session: Session) {
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelScreenState.ChannelMessagesError(
                    Problem.NoConnection
                )
                null
            },
            request = {
                services.channelService.deleteChannel(channel, session)
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem
                    )
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelDeleted
                )
            },
            refresh = services.authService::refresh
        )
    }

    suspend fun fetchMessages(
        paginationRequest: PaginationRequest,
        currentItems: List<Message>,
    ): Either<Problem, Pagination<Message>> {

        if (channel == null) {
            return failure(Problem.UnexpectedProblem)
        }

        var result: Either<Problem, Pagination<Message>> = failure(Problem.UnexpectedProblem)
        val job = launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                storage.messageRepository.getChannelMessages(
                    channel!!,
                    paginationRequest.limit,
                    getCount = false,
                    before = currentItems.lastOrNull()?.createdAt,
                )
            },
            request = { session ->
                services.messageService.getChannelMessages(
                    channel!!,
                    session,
                    paginationRequest,
                    sort = null,
                    before = currentItems.lastOrNull()?.createdAt,

                    )
            },
            refresh = services.authService::refresh,
            onError = {
                result = failure(it)
            },
            onSuccess = {
                result = it
                if (ChimpApplication.applicationContext().isNetworkAvailable()) {
                    storage.messageRepository.updateMessages(it.value.items)
                }
            }
        )
        job.join()
        return result
    }
}