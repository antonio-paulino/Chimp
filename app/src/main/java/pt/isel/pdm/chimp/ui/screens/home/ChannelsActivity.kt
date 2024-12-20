package pt.isel.pdm.chimp.ui.screens.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.infrastructure.SSEService
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.navigation.navigateToNoAnimation
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.screens.channel.editChannel.EditChannelActivity
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsActivity
import pt.isel.pdm.chimp.ui.screens.home.createChannel.CreateChannelActivity
import pt.isel.pdm.chimp.ui.screens.home.inviteUser.InviteUserActivity
import pt.isel.pdm.chimp.ui.screens.invitations.InvitationsActivity
import pt.isel.pdm.chimp.ui.screens.search.ChannelSearchActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import kotlin.time.Duration.Companion.seconds

open class ChannelsActivity : ComponentActivity() {
    lateinit var dependencies: DependenciesContainer
    private var isListening = false

    private val channelsViewModel by initializeViewModel { dependencies ->
        ChannelsViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
            dependencies.storage,
        )
    }

    private val scrollingViewModel by initializeViewModel { _ ->
        InfiniteScrollViewModel(
            fetchItemsRequest = channelsViewModel::fetchChannels,
            limit = 20,
            getCount = false,
            useOffset = false,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dependencies = application as DependenciesContainer
        FirebaseApp.initializeApp(this)
        setContent {
            val session by dependencies.sessionManager.session.collectAsState(
                initial =
                    runBlocking {
                        dependencies.sessionManager.session.firstOrNull()
                    },
            )
            val channelState by channelsViewModel.state.collectAsState(initial = ChannelsScreenState.ChannelsList)
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Loading(Pagination<Channel>()))
            ChIMPTheme {
                ChannelsScreen(
                    channelState = channelState,
                    scrollState = scrollState,
                    session = session,
                    onNotLoggedIn = {
                        navigateTo(CredentialsActivity::class.java)
                        finish()
                    },
                    onLoggedIn = {
                        if (!isListening) {
                            startListening()
                        }
                    },
                    onBottomScroll = scrollingViewModel::loadMore,
                    onChannelSelected = { channel ->
                        dependencies.entityReferenceManager.setChannel(channel)
                        navigateTo(EditChannelActivity::class.java)
                    },
                    onLogout = channelsViewModel::logout,
                    onAboutNavigation = { navigateToNoAnimation(AboutActivity::class.java) },
                    onInvitationsNavigation = { navigateToNoAnimation(InvitationsActivity::class.java) },
                    onCreateChannelNavigation = { navigateTo(CreateChannelActivity::class.java) },
                    onInviteUserNavigation = { navigateTo(InviteUserActivity::class.java) },
                    onSearchNavigation = { navigateToNoAnimation(ChannelSearchActivity::class.java) },
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : ViewModel> initializeViewModel(
        crossinline constructor: (
            dependencies: DependenciesContainer,
        ) -> T,
    ): Lazy<T> {
        val factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return constructor(
                        dependencies,
                    ) as T
                }
            }
        return viewModels<T>(factoryProducer = { factory })
    }

    private fun startListening() {
        val intent = Intent(this, SSEService::class.java)
        startService(intent)
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
        isListening = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isListening) {
            val intent = Intent(this, SSEService::class.java)
            stopService(intent)
            isListening = false
        }
        Log.v(TAG, "MainActivity.onDestroy")
    }

    private suspend fun handleChannelDeleted(event: Event.ChannelEvent.DeletedEvent) {
        scrollingViewModel.handleItemDelete(event.channelId)
        val referencedChannel = dependencies.entityReferenceManager.channel.firstOrNull()
        if (referencedChannel != null && referencedChannel.id == event.channelId) {
            dependencies.entityReferenceManager.setChannel(null)
        }
        dependencies.storage.channelRepository.deleteChannel(event.channelId)
    }

    private suspend fun handleChannelUpdated(event: Event.ChannelEvent.UpdatedEvent) {
        if (event.channel.members.none { it.id == dependencies.sessionManager.session.firstOrNull()?.user?.id }) {
            scrollingViewModel.handleItemDelete(event.channel.id)
            dependencies.storage.channelRepository.deleteChannel(event.channel.id)
        } else {
            val state = scrollingViewModel.state.firstOrNull()
            val paginationItems = state?.pagination?.items
            val finished = state?.pagination?.info?.nextPage == null
            if (
                paginationItems?.any { it.id == event.channel.id } == false &&
                ((paginationItems.lastOrNull()?.id?.value ?: 0) > event.channel.id.value || finished)
            ) {
                scrollingViewModel.handleItemCreate(event.channel)
            } else {
                scrollingViewModel.handleItemUpdate(event.channel)
            }
            dependencies.storage.channelRepository.updateChannels(listOf(event.channel))
        }
        val referencedChannel = dependencies.entityReferenceManager.channel.firstOrNull()
        if (referencedChannel != null && referencedChannel.id == event.channel.id) {
            dependencies.entityReferenceManager.setChannel(event.channel)
        }
    }

    private suspend fun handleChannelCreated(event: Event.ChannelEvent.CreatedEvent) {
        scrollingViewModel.handleItemCreate(event.channel)
        dependencies.storage.channelRepository.updateChannels(listOf(event.channel))
        val referencedChannel = dependencies.entityReferenceManager.channel.firstOrNull()
        if (referencedChannel != null && referencedChannel.id == event.channel.id) {
            dependencies.entityReferenceManager.setChannel(event.channel)
        }
    }
}
