package pt.isel.pdm.chimp.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.DependenciesActivity
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.navigation.navigateToNoAnimation
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.screens.channel.ChannelActivity
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsActivity
import pt.isel.pdm.chimp.ui.screens.home.createChannel.CreateChannelActivity
import pt.isel.pdm.chimp.ui.screens.home.inviteUser.InviteUserActivity
import pt.isel.pdm.chimp.ui.screens.invitations.InvitationsActivity
import pt.isel.pdm.chimp.ui.screens.search.ChannelSearchActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import kotlin.time.Duration.Companion.seconds

open class ChannelsActivity : DependenciesActivity() {
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
        startListening()
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
                        if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                            checkNotificationPermission()
                        }
                    },
                    onBottomScroll = scrollingViewModel::loadMore,
                    onChannelSelected = { channel ->
                        dependencies.entityReferenceManager.setChannel(channel)
                        navigateTo(ChannelActivity::class.java)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val hasRequestedPermission = sharedPreferences.getBoolean("requested_notification_permission", false)
        if (
            !hasRequestedPermission &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            sharedPreferences.edit().putBoolean("requested_notification_permission", true).apply()
        }
    }

    private suspend fun handleChannelDeleted(event: Event.ChannelEvent.DeletedEvent) {
        scrollingViewModel.handleItemDelete(event.channelId)
        val referencedChannel = dependencies.entityReferenceManager.channel.firstOrNull()
        if (referencedChannel != null && referencedChannel.id == event.channelId) {
            dependencies.entityReferenceManager.setChannel(null)
        }
    }

    private suspend fun handleChannelUpdated(event: Event.ChannelEvent.UpdatedEvent) {
        if (event.channel.members.none { it.id == dependencies.sessionManager.session.firstOrNull()?.user?.id }) {
            scrollingViewModel.handleItemDelete(event.channel.id)
            if (dependencies.entityReferenceManager.channel.firstOrNull()?.id == event.channel.id) {
                dependencies.entityReferenceManager.setChannel(null)
            }
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
        }
        val referencedChannel = dependencies.entityReferenceManager.channel.firstOrNull()
        if (referencedChannel != null && referencedChannel.id == event.channel.id) {
            dependencies.entityReferenceManager.setChannel(event.channel)
        }
    }

    private suspend fun handleChannelCreated(event: Event.ChannelEvent.CreatedEvent) {
        scrollingViewModel.handleItemCreate(event.channel)
        val referencedChannel = dependencies.entityReferenceManager.channel.firstOrNull()
        if (referencedChannel != null && referencedChannel.id == event.channel.id) {
            dependencies.entityReferenceManager.setChannel(event.channel)
        }
    }
}
