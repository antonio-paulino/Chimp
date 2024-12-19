package pt.isel.pdm.chimp.ui.screens.search

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
import pt.isel.pdm.chimp.ui.navigation.navigateToNoAnimation
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.screens.invitations.InvitationsActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import kotlin.time.Duration.Companion.seconds

open class ChannelSearchActivity : ChannelsActivity() {

    private val viewModel by initializeViewModel { dependencies ->
        ChannelSearchViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
        )
    }

    private val scrollingViewModel by initializeViewModel { _ ->
        InfiniteScrollViewModel(
            fetchItemsRequest = viewModel::fetchChannelsByName,
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
            val channelsState = viewModel.state.collectAsState(initial = ChannelSearchListScreenState.ChannelSearchList)
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Initial())
            val user = runBlocking { dependencies.sessionManager.session.firstOrNull()!!.user }
            val searchField = viewModel.searchQuery.collectAsState(initial = "")
            ChIMPTheme {
                ChannelSearchScreen(
                    state = channelsState.value,
                    scrollState = scrollState,
                    user = user,
                    searchField = searchField.value,
                    onSearchValueChange = viewModel::setSearchQuery,
                    doSearch = scrollingViewModel::reset,
                    onScrollToBottom = scrollingViewModel::loadMore,
                    onJoinChannel =  { channel -> viewModel.joinChannel(channel, onJoin = { scrollingViewModel.handleItemDelete(channel.id) }) },
                    onHomeNavigation = { navigateToNoAnimation(ChannelsActivity::class.java) },
                    onInvitationsNavigation = { navigateToNoAnimation(InvitationsActivity::class.java) },
                    onAboutNavigation = { navigateToNoAnimation(AboutActivity::class.java)}
                )
            }
        }
    }

    private fun startListening() {
        this.lifecycleScope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.channelEventFlow.collect { event ->
                when (event) {
                    is Event.ChannelEvent.DeletedEvent -> handleChannelDeleted(event)
                    is Event.ChannelEvent.UpdatedEvent -> handleChannelUpdated(event)
                    is Event.ChannelEvent.CreatedEvent -> handleChannelCreated(event)
                }
            }
        }
    }

    private suspend fun handleChannelDeleted(event: Event.ChannelEvent.DeletedEvent) {
        scrollingViewModel.handleItemDelete(event.channelId)
    }

    private suspend fun handleChannelUpdated(event: Event.ChannelEvent.UpdatedEvent) {
        scrollingViewModel.handleItemCreate(event.channel)
    }

    private suspend fun handleChannelCreated(event: Event.ChannelEvent.CreatedEvent) {
        scrollingViewModel.handleItemCreate(event.channel)
    }
}
