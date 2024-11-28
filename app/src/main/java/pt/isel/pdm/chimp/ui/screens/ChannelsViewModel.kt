package pt.isel.pdm.chimp.ui.screens

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.future.asDeferred
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.infrastructure.storage.Storage
import pt.isel.pdm.chimp.ui.utils.launchRequestRefreshing
import java.util.concurrent.CompletableFuture

interface ChannelScreenState {
    data class CreationError(
        val channelName: String,
        val isPublic: Boolean,
        val defaultRole: ChannelRole,
        val message: String,
    ) : ChannelScreenState

    data class CreatingChannel(val channelName: String, val isPublic: Boolean, val defaultRole: ChannelRole) : ChannelScreenState

    data object ChannelsListAll : ChannelScreenState
}

open class ChannelsViewModel(
    private val services: ChimpService,
    private val sessionManager: SessionManager,
    private val storage: Storage,
) : ViewModel() {
    private val _state: MutableStateFlow<ChannelScreenState> = MutableStateFlow(ChannelScreenState.ChannelsListAll)

    val state = _state

    fun startListening(
        scope: LifecycleCoroutineScope,
        session: Session,
    ) {
        Log.d(TAG, "ChannelsViewModel.startListening")
        services.eventService.initialize(scope, session)
    }

    fun stopListening() {
        services.eventService.destroy()
    }

    suspend fun fetchChannels(
        paginationRequest: PaginationRequest,
        currentItems: List<Channel>,
    ): Either<Problem, Pagination<Channel>> {
        val result = CompletableFuture<Either<Problem, Pagination<Channel>>>()
        launchRequestRefreshing(
            noConnectionRequest = {
                storage.channelRepository.getChannels(
                    pagination = paginationRequest,
                    sort = null,
                    after = currentItems.lastOrNull()?.id,
                    filterOwned = false,
                )
            },
            request = {
                services.channelService.getChannels(
                    name = null,
                    pagination = paginationRequest,
                    sort = null,
                    session = sessionManager.session.last()!!,
                    after = currentItems.lastOrNull()?.id,
                    filterOwned = false,
                )
            },
            refresh = {
                val session = sessionManager.session.last()!!
                if (session.expired) {
                    services.authService.refresh(session)
                }
            },
            onError = { result.complete(failure(it)) },
            onSuccess = { result.complete(it) },
        )
        return result.asDeferred().await()
    }
}
