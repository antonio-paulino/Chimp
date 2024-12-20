package pt.isel.pdm.chimp.ui.screens.channel.channelMembers

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing

sealed interface ChannelMembersScreenState {
    data object ChannelMembersList : ChannelMembersScreenState

    data object Loading : ChannelMembersScreenState

    data class MemberRemoved(val member: ChannelMember) : ChannelMembersScreenState

    data class MemberRoleChanged(val member: ChannelMember) : ChannelMembersScreenState

    data class ChannelMembersListError(val problem: Problem) : ChannelMembersScreenState
}

class ChannelMembersViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    initialScreenState: ChannelMembersScreenState = ChannelMembersScreenState.ChannelMembersList,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelMembersScreenState> = MutableStateFlow(initialScreenState)

    val state: Flow<ChannelMembersScreenState> = _state

    fun removeMember(
        channel: Channel,
        member: ChannelMember,
    ) {
        _state.value = ChannelMembersScreenState.Loading
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelMembersScreenState.ChannelMembersListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.channelService.removeMemberFromChannel(
                    session = session,
                    channel = channel,
                    member = member,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelMembersScreenState.ChannelMembersListError(it)) },
            onSuccess = { _state.emit(ChannelMembersScreenState.MemberRemoved(member)) },
        )
    }

    fun updateMemberRole(
        channel: Channel,
        member: ChannelMember,
        role: ChannelRole,
    ) {
        if (member.role == role) return
        _state.value = ChannelMembersScreenState.Loading
        launchRequestRefreshing(
            sessionManager = sessionManager,
            noConnectionRequest = {
                _state.value = ChannelMembersScreenState.ChannelMembersListError(Problem.NoConnection)
                null
            },
            request = { session ->
                services.channelService.updateMemberRole(
                    session = session,
                    channel = channel,
                    member = member,
                    role = role,
                )
            },
            refresh = services.authService::refresh,
            onError = { _state.emit(ChannelMembersScreenState.ChannelMembersListError(it)) },
            onSuccess = { _state.emit(ChannelMembersScreenState.MemberRoleChanged(member.copy(role = role))) },
        )
    }
}
