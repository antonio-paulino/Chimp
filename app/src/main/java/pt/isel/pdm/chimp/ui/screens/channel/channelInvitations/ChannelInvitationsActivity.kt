package pt.isel.pdm.chimp.ui.screens.channel.channelInvitations

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import kotlin.time.Duration.Companion.seconds

class ChannelInvitationsActivity : ChannelsActivity() {
    private val channelInvitationsViewModel by initializeViewModel { dependencies ->
        ChannelInvitationsViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
            channel = runBlocking { dependencies.entityReferenceManager.channel.firstOrNull() },
        )
    }

    private val scrollingViewModel by initializeViewModel { _ ->
        InfiniteScrollViewModel(
            fetchItemsRequest = channelInvitationsViewModel::fetchInvitations,
            limit = 20,
            getCount = false,
            useOffset = false,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startListening()
        dependencies = application as DependenciesContainer
        setContent {
            val viewModelState by channelInvitationsViewModel.state.collectAsState(
                initial = ChannelInvitationsScreenState.ChannelInvitationsList,
            )
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Initial())
            val channel =
                dependencies.entityReferenceManager.channel.collectAsState(
                    initial =
                        runBlocking {
                            dependencies.entityReferenceManager.channel.firstOrNull()
                        },
                ).value
            channelInvitationsViewModel.setChannel(channel)
            ChIMPTheme {
                ChannelInvitationsScreen(
                    state = viewModelState,
                    scrollState = scrollState,
                    onChannelNull = { finish() },
                    onRemoveInvitation = { invitation -> channelInvitationsViewModel.deleteInvitation(invitation) },
                    onUpdateInvitationRole = { invitation, role -> channelInvitationsViewModel.updateInvitationRole(invitation, role) },
                    onBack = { finish() },
                    loadMore = { scrollingViewModel.loadMore() },
                    channel = channelInvitationsViewModel.channel,
                )
            }
        }
    }

    private fun startListening() {
        this.lifecycleScope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.invitationEventFlow.collect { event ->
                when (event) {
                    is Event.InvitationEvent.CreatedEvent -> handleInvitationCreated(event)
                    is Event.InvitationEvent.DeletedEvent -> handleInvitationDeleted(event)
                    is Event.InvitationEvent.UpdatedEvent -> handleInvitationUpdated(event)
                }
            }
        }
    }

    private fun handleInvitationCreated(event: Event.InvitationEvent.CreatedEvent) {
        if (event.invitation.channel.id == channelInvitationsViewModel.channel?.id) {
            scrollingViewModel.handleItemCreate(event.invitation)
        }
    }

    private suspend fun handleInvitationDeleted(event: Event.InvitationEvent.DeletedEvent) {
        scrollingViewModel.handleItemDelete(event.invitationId)
    }

    private suspend fun handleInvitationUpdated(event: Event.InvitationEvent.UpdatedEvent) {
        scrollingViewModel.handleItemUpdate(event.invitation)
    }
}
