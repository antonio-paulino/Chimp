package pt.isel.pdm.chimp.ui.screens

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
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

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
            val session by dependencies.sessionManager.session.collectAsState(initial = null)
            val channelState by channelsViewModel.state.collectAsState(initial = ChannelScreenState.ChannelsListAll)
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Loading(Pagination<Channel>()))
            Log.d(TAG, "ChannelsActivity.setContent: $session")
            ChIMPTheme {
                ChannelsScreen(
                    channelState = channelState,
                    scrollState = scrollState,
                    onAboutNavigation = { navigateTo(AboutActivity::class.java) },
                    onNotLoggedIn = {
                        navigateTo(CredentialsActivity::class.java)
                        finish()
                    },
                    onLoggedIn = { session ->
                        if (!isListening) {
                            channelsViewModel.startListening(lifecycleScope, session)
                            Log.d(TAG, "this.lifecycleScope: $lifecycleScope")
                            this.lifecycleScope.launch { // Just for testing purposes, might be removed
                                dependencies.chimpService.eventService.eventFlow.collect { event ->
                                    Log.d(TAG, "Event received: $event")
                                }
                            }
                            isListening = true
                        }
                    },
                    session = session,
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

    override fun onDestroy() {
        super.onDestroy()
        if (isListening) {
            channelsViewModel.stopListening()
            isListening = false
        }
        Log.v(TAG, "MainActivity.onDestroy")
    }
}
