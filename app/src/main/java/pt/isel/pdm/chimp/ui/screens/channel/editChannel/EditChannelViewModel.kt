package pt.isel.pdm.chimp.ui.screens.channel.editChannel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface EditChannelScreenState {
    data object EditingChannel : EditChannelScreenState

    data class EditingChannelError(
        val problem: Problem,
    ) : EditChannelScreenState

    data object Loading : EditChannelScreenState

    data object Success : EditChannelScreenState
}

class EditChannelViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    initialScreenState: EditChannelScreenState = EditChannelScreenState.EditingChannel,
) : ViewModel() {
    private val _state: MutableStateFlow<EditChannelScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<EditChannelScreenState> = _state

    fun updateChannel(
        channel: Channel,
        name: Name,
        isPublic: Boolean,
        defaultRole: ChannelRole,
    ) {
        _state.value = EditChannelScreenState.Loading
        launchRequestRefreshing(
            noConnectionRequest = {
                _state.emit(
                    EditChannelScreenState.EditingChannelError(Problem.NoConnection),
                )
                null
            },
            request = { session ->
                services.channelService.updateChannel(
                    channel.id,
                    name,
                    defaultRole,
                    isPublic,
                    session,
                )
            },
            onError = {
                _state.emit(
                    EditChannelScreenState.EditingChannelError(it),
                )
            },
            onSuccess = { _state.emit(EditChannelScreenState.Success) },
            sessionManager = sessionManager,
            refresh = services.authService::refresh,
        )
    }
}
