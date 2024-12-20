package pt.isel.pdm.chimp.ui.screens.channel

import ChannelScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.screens.channel.channelInvitations.ChannelInvitationsActivity
import pt.isel.pdm.chimp.ui.screens.channel.channelMembers.ChannelMembersActivity
import pt.isel.pdm.chimp.ui.screens.channel.editChannel.EditChannelActivity
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsActivity
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import kotlin.time.Duration.Companion.seconds

open class ChannelActivity : ChannelsActivity() {

    private val viewModel by initializeViewModel { dependencies ->
        ChannelViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
            dependencies.storage,
            channel = runBlocking { dependencies.entityReferenceManager.channel.firstOrNull() },
        )
    }

    private val scrollingViewModel by initializeViewModel { _ ->
        InfiniteScrollViewModel(
            fetchItemsRequest = viewModel::fetchMessages,
            limit = 20,
            getCount = false,
            useOffset = false,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startListening()
        setContent {
            val session by dependencies.sessionManager.session.collectAsState(
                initial =
                runBlocking {
                    dependencies.sessionManager.session.firstOrNull()
                }
            )
            val channel by dependencies.entityReferenceManager.channel.collectAsState(
                initial =
                runBlocking {
                    dependencies.entityReferenceManager.channel.firstOrNull()
                }
            )
            viewModel.setChannel(channel)
            val state by viewModel.state.collectAsState(initial = ChannelScreenState.MessagesList)
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Initial())
            ChIMPTheme {
                ChannelScreen(
                    state = state,
                    channel = channel,
                    scrollState = scrollState,
                    onBottomScroll = scrollingViewModel::loadMore,
                    onBack = { finish() },
                    onSendMessage = { message ->
                        viewModel.createMessage(
                            channel!!,
                            message,
                            session!!
                        )
                    },
                    onEditChannel = {
                        navigateTo(EditChannelActivity::class.java)
                    },
                    onInviteMember =
                    {
                        //navigateTo(::class.java)
                    },
                    onChannelDelete = {
                        viewModel.deleteChannel(channel!!, session!!)
                    },
                    onChannelMembers = { navigateTo(ChannelMembersActivity::class.java) },
                    onManageInvitations = { navigateTo(ChannelInvitationsActivity::class.java) },
                )
            }
        }
    }

    private fun startListening() {
        this.lifecycleScope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.messageEventFlow.collect { event ->
                when (event) {
                    is Event.MessageEvent.CreatedEvent -> handleMessageCreated(event)
                    is Event.MessageEvent.DeletedEvent -> handleMessageDeleted(event)
                    is Event.MessageEvent.UpdatedEvent -> handleMessageUpdated(event)
                }
            }
        }
    }

    private fun handleMessageCreated(event: Event.MessageEvent.CreatedEvent) {
        if (event.message.channelId == viewModel.channel?.id) {
            scrollingViewModel.handleItemCreate(event.message)
        }
    }

    private suspend fun handleMessageDeleted(event: Event.MessageEvent.DeletedEvent) {
        scrollingViewModel.handleItemDelete(event.messageId)
    }

    private suspend fun handleMessageUpdated(event: Event.MessageEvent.UpdatedEvent) {
        scrollingViewModel.handleItemUpdate(event.message)
    }
}