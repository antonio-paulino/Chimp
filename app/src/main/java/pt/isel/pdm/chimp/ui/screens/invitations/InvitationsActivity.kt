package pt.isel.pdm.chimp.ui.screens.invitations
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.navigation.navigateToNoAnimation
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import kotlin.time.Duration.Companion.seconds

open class InvitationsActivity : ChannelsActivity() {

    private val invitationsViewModel by initializeViewModel { dependencies ->
        InvitationsViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    private val scrollingViewModel by initializeViewModel { _ ->
        InfiniteScrollViewModel(
            fetchItemsRequest = invitationsViewModel::fetchInvitations,
            limit = 20,
            getCount = false,
            useOffset = false,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dependencies = application as DependenciesContainer
        startListening()
        setContent {
            val invitationsState by invitationsViewModel.state.collectAsState(initial = InvitationsScreenState.InvitationsList)
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Initial())
            ChIMPTheme {
                InvitationsScreen(
                    state = invitationsState,
                    scrollState = scrollState,
                    onAcceptInvitation = { invitation -> invitationsViewModel.acceptInvitation(invitation) },
                    onRejectInvitation = { invitation -> invitationsViewModel.rejectInvitation(invitation) },
                    onScrollToBottom = { scrollingViewModel.loadMore() },
                    onAboutNavigation = { navigateToNoAnimation(AboutActivity::class.java) },
                    onHomeNavigation = { navigateToNoAnimation(ChannelsActivity::class.java) },
                )
            }
        }
    }

    private fun startListening() {
        this.lifecycleScope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.invitationEventFlow.collect { event ->
                when (event) {
                    is Event.InvitationEvent.DeletedEvent -> handleInvitationDeleted(event)
                    is Event.InvitationEvent.UpdatedEvent -> handleInvitationUpdated(event)
                    is Event.InvitationEvent.CreatedEvent -> handleInvitationCreated(event)
                }
            }
        }
    }

    private suspend fun handleInvitationDeleted(event: Event.InvitationEvent.DeletedEvent) {
        scrollingViewModel.handleItemDelete(event.invitationId)
    }

    private suspend fun handleInvitationCreated(event: Event.InvitationEvent.CreatedEvent) {
        scrollingViewModel.handleItemUpdate(event.invitation)
    }

    private suspend fun handleInvitationUpdated(event: Event.InvitationEvent.UpdatedEvent) {
        scrollingViewModel.handleItemUpdate(event.invitation)
    }
}
