package pt.isel.pdm.chimp.ui.components.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals

@Composable
fun <T : Identifiable> InfiniteScroll(
    scrollState: InfiniteScrollState<T>,
    loadMore: () -> Unit,
    reverse: Boolean = false,
    renderItem: @Composable (T) -> Unit,
) {
    val listState = rememberLazyListState()
    val snackBarHost = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHost) },
    ) { innerPadding ->
        if (scrollState is InfiniteScrollState.Loading && scrollState.pagination.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingSpinner()
            }
        }
        LazyColumn(
            reverseLayout = reverse,
            state = listState,
            contentPadding = innerPadding,
        ) {
            items(
                count = scrollState.pagination.items.size,
                key = { index -> scrollState.pagination.items[index].id.value },
            ) { itemIndex ->
                renderItem(scrollState.pagination.items[itemIndex])
                if (itemIndex == scrollState.pagination.items.size - 1) {
                    if (scrollState is InfiniteScrollState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            LoadingSpinner()
                        }
                    }
                }
            }
        }
    }

    val isAtBottom =
        remember {
            derivedStateOf {
                listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size >= listState.layoutInfo.totalItemsCount
            }
        }
    val itemCount = scrollState.pagination.items.size
    val previousItemCount = remember { mutableIntStateOf(0) }

    LaunchedEffect(itemCount) {
        if (itemCount == previousItemCount.intValue + 1 && isAtBottom.value) {
            listState.animateScrollToItem(itemCount - 1)
        }
        previousItemCount.intValue = itemCount
    }

    if (isAtBottom.value && scrollState !is InfiniteScrollState.Loading && scrollState.pagination.info.nextPage != null) {
        loadMore()
    }

    LaunchedEffect(scrollState) {
        if (scrollState is InfiniteScrollState.Error) {
            snackBarHost.showSnackbar(SnackBarVisuals(scrollState.problem.detail, true))
        }
    }
}
