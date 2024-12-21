package pt.isel.pdm.chimp.ui.screens.home.createChannel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface CreateChannelScreenState {
    sealed interface CreateChannelForm : CreateChannelScreenState {
        val name: String
        val isPublic: Boolean
        val defaultRole: ChannelRole
    }

    data class CreatingChannel(
        override val name: String = "",
        override val isPublic: Boolean = false,
        override val defaultRole: ChannelRole = ChannelRole.MEMBER,
    ) : CreateChannelForm

    data class CreatingChannelError(
        override val name: String = "",
        override val isPublic: Boolean = false,
        override val defaultRole: ChannelRole = ChannelRole.MEMBER,
        val problem: Problem,
    ) : CreateChannelForm

    data object Loading : CreateChannelScreenState

    data object Success : CreateChannelScreenState
}

class CreateChannelViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    initialScreenState: CreateChannelScreenState =
        CreateChannelScreenState.CreatingChannel(
            "",
            false,
            ChannelRole.MEMBER,
        ),
) : ViewModel() {
    private val _state: MutableStateFlow<CreateChannelScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<CreateChannelScreenState> = _state

    fun createChannel(
        name: Name,
        isPublic: Boolean,
        defaultRole: ChannelRole,
    ) {
        _state.value = CreateChannelScreenState.Loading
        launchRequestRefreshing(
            noConnectionRequest = {
                _state.emit(
                    CreateChannelScreenState.CreatingChannelError(
                        name.value,
                        isPublic,
                        defaultRole,
                        Problem.NoConnection,
                    ),
                )
                null
            },
            request = {
                services.channelService.createChannel(
                    name,
                    defaultRole,
                    isPublic,
                    sessionManager.session.first()!!,
                )
            },
            onError = {
                _state.emit(
                    CreateChannelScreenState.CreatingChannelError(
                        name.value,
                        isPublic,
                        defaultRole,
                        it,
                    ),
                )
            },
            onSuccess = { _state.emit(CreateChannelScreenState.Success) },
            sessionManager = sessionManager,
            refresh = services.authService::refresh,
        )
    }
}
