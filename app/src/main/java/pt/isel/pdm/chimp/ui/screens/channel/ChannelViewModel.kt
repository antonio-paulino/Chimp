package pt.isel.pdm.chimp.ui.screens.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.Sort
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManager
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.infrastructure.storage.Storage
import pt.isel.pdm.chimp.ui.utils.isNetworkAvailable
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface ChannelScreenState {
    data class ChannelMessageCreated(val message: Message) : ChannelScreenState

    data class ChannelMessagesError(val problem: Problem) : ChannelScreenState

    data object SendingMessage : ChannelScreenState

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
    private val entityReferenceManager: EntityReferenceManager,
    initialScreenState: ChannelScreenState = ChannelScreenState.MessagesList,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelScreenState> = _state

    fun createMessage(
        channel: Channel,
        message: String,
    ) {
        _state.value = ChannelScreenState.SendingMessage
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value =
                    ChannelScreenState.ChannelMessagesError(
                        Problem.NoConnection,
                    )
                null
            },
            request = { session ->
                services.messageService.createMessage(channel, message, session)
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem,
                    ),
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelMessageCreated(
                        it.value,
                    ),
                )
            },
            refresh = services.authService::refresh,
        )
    }

    fun updateMessage(
        message: Message,
        content: String,
    ) {
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value =
                    ChannelScreenState.ChannelMessagesError(
                        Problem.NoConnection,
                    )
                null
            },
            request = { session ->
                services.messageService.updateMessage(message, content, session)
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem,
                    ),
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelMessageUpdated,
                )
            },
            refresh = services.authService::refresh,
        )
    }

    fun deleteMessage(message: Message) {
        viewModelScope.launch {
            launchRequestRefreshing(
                sessionManager = sessionManager,
                noConnectionRequest = {
                    _state.value =
                        ChannelScreenState.ChannelMessagesError(
                            Problem.NoConnection,
                        )
                    null
                },
                request = { session ->
                    services.messageService.deleteMessage(message, session)
                },
                onError = { problem ->
                    _state.emit(
                        ChannelScreenState.ChannelMessagesError(
                            problem,
                        ),
                    )
                },
                onSuccess = {
                    _state.emit(
                        ChannelScreenState.ChannelMessageDeleted,
                    )
                },
                refresh = services.authService::refresh,
            )
        }
    }

    fun deleteChannel(channel: Channel) {
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value =
                    ChannelScreenState.ChannelMessagesError(
                        Problem.NoConnection,
                    )
                null
            },
            request = { session ->
                services.channelService.deleteChannel(channel, session)
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem,
                    ),
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelDeleted,
                )
            },
            refresh = services.authService::refresh,
        )
    }

    suspend fun fetchMessages(
        paginationRequest: PaginationRequest,
        currentItems: List<Message>,
    ): Either<Problem, Pagination<Message>> {
        var result: Either<Problem, Pagination<Message>> = failure(Problem.UnexpectedProblem)
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                storage.messageRepository.getChannelMessages(
                    channel =
                        entityReferenceManager.channel.firstOrNull()
                            ?: return@launchRequestRefreshing failure(Problem.UnexpectedProblem),
                    paginationRequest.limit,
                    getCount = false,
                    before = currentItems.lastOrNull()?.createdAt,
                )
            },
            request = { session ->
                services.messageService.getChannelMessages(
                    channel =
                        entityReferenceManager.channel.firstOrNull()
                            ?: return@launchRequestRefreshing failure(Problem.UnexpectedProblem),
                    session,
                    paginationRequest,
                    sort =
                        SortRequest(
                            "createdAt",
                            Sort.DESC,
                        ),
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
            },
        ).join()
        return result
    }

    fun leaveChannel(channel: Channel) {
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value =
                    ChannelScreenState.ChannelMessagesError(
                        Problem.NoConnection,
                    )
                null
            },
            request = { session ->
                services.channelService.removeMemberFromChannel(
                    channel,
                    channel.getMember(session.user)!!,
                    session,
                )
            },
            onError = { problem ->
                _state.emit(
                    ChannelScreenState.ChannelMessagesError(
                        problem,
                    ),
                )
            },
            onSuccess = {
                _state.emit(
                    ChannelScreenState.ChannelDeleted,
                )
            },
            refresh = services.authService::refresh,
        )
    }

    fun onToggleEdit(message: Message?) {
        if (
            (
                _state.value is ChannelScreenState.EditingMessage &&
                    (_state.value as ChannelScreenState.EditingMessage).message == message
            ) || message == null
        ) {
            _state.value = ChannelScreenState.MessagesList
        } else {
            _state.value = ChannelScreenState.EditingMessage(message)
        }
    }
}
