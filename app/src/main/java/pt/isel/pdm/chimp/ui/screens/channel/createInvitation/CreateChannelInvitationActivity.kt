package pt.isel.pdm.chimp.ui.screens.channel.createInvitation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollViewModel
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

class CreateChannelInvitationActivity : ChannelsActivity() {
    private val viewModel by initializeViewModel { dependencies ->
        CreateChannelInvitationViewModel(
            dependencies.chimpService,
            dependencies.sessionManager,
            dependencies.entityReferenceManager,
        )
    }

    private val scrollingViewModel by initializeViewModel { _ ->
        InfiniteScrollViewModel(
            fetchItemsRequest = viewModel::fetchUsers,
            limit = 20,
            getCount = false,
            useOffset = true,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dependencies = application as DependenciesContainer
        setContent {
            val vmState = viewModel.state.collectAsState(initial = CreateChannelInvitationScreenState.SearchingUsers())
            val scrollState by scrollingViewModel.state.collectAsState(initial = InfiniteScrollState.Initial())
            val session by dependencies.sessionManager.session.collectAsState(
                initial = runBlocking { dependencies.sessionManager.session.firstOrNull() },
            )
            val channel by dependencies.entityReferenceManager.channel.collectAsState(
                initial = runBlocking { dependencies.entityReferenceManager.channel.firstOrNull() },
            )
            val searchField = viewModel.searchQuery.collectAsState(initial = "")
            ChIMPTheme {
                CreateChannelInvitationScreen(
                    channel = channel,
                    state = vmState.value,
                    scrollState = scrollState,
                    user = session?.user,
                    searchField = searchField.value,
                    onSearchValueChange = viewModel::setSearchQuery,
                    doSearch = scrollingViewModel::reset,
                    onScrollToBottom = scrollingViewModel::loadMore,
                    onInviteUser = viewModel::submitInvitation,
                    onBack = { finish() },
                )
            }
        }
    }
}
