package pt.isel.pdm.chimp.ui.screens

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.infrastructure.SSEService
import pt.isel.pdm.chimp.infrastructure.services.http.events.Event
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsActivity
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

    private fun startListening() {
        val intent = Intent(this, SSEService::class.java)
        startService(intent)
        this.lifecycleScope.launch {
            dependencies.chimpService.eventService.awaitInitialization(30.seconds)
            dependencies.chimpService.eventService.channelEventFlow.collect { event ->
                when (event) {
                    is Event.ChannelEvent.DeletedEvent -> {
                        scrollingViewModel.handleItemDelete(event.channelId)
                    }
                    is Event.ChannelEvent.UpdatedEvent -> {
                        scrollingViewModel.handleItemUpdate(event.channel)
                    }
                }
            }
        }
        isListening = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dependencies = application as DependenciesContainer
        FirebaseApp.initializeApp(this)
        setContent {
            val session by dependencies.sessionManager.session.collectAsState(initial = runBlocking { dependencies.sessionManager.session.first() })
            val channelState by channelsViewModel.state.collectAsState(initial = ChannelScreenState.ChannelsList)
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Loading(Pagination<Channel>()))
            ChIMPTheme {
                ChannelsScreen(
                    channelState = channelState,
                    scrollState = scrollState,
                    onAboutNavigation = { navigateTo(AboutActivity::class.java) },
                    onNotLoggedIn = {
                        navigateTo(CredentialsActivity::class.java)
                        finish()
                    },
                    onLoggedIn = {
                        if (!isListening) {
                            startListening()
                        }
                    },
                    session = session,
                    loadMore = { scrollingViewModel.loadMore() },
                    onChannelSelected = { channel ->
                        dependencies.entityReferenceManager.set(channel)
                        // navigateTo(ChannelActivity::class.java)
                    },
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

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "MainActivity.onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "MainActivity.onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "MainActivity.onResume")
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
}
