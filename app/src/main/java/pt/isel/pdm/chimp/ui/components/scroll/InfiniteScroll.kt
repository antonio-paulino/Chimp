package pt.isel.pdm.chimp.ui.components.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.utils.showErrorToast

@Composable
fun <T: Identifiable> InfiniteScroll(
    scrollState: InfiniteScrollState<T>,
    loadMore: () -> Unit,
    renderItem: @Composable (T) -> Unit
) {
    when {
        scrollState.pagination.items.isEmpty() && scrollState !is InfiniteScrollState.Loading -> return
        scrollState is InfiniteScrollState.Error -> showErrorToast(scrollState.problem.detail)
        scrollState.pagination.items.isEmpty() && scrollState is InfiniteScrollState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LoadingSpinner()
            }
        }
    }
    val listState = rememberLazyListState()
    val isAtBottom = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size >= listState.layoutInfo.totalItemsCount
        }
    }
    LazyColumn(
        state = listState,
    ) {
        items(
            count = scrollState.pagination.items.size,
            key = { index -> scrollState.pagination.items[index].id.value },
        ) { itemIndex ->
            renderItem(scrollState.pagination.items[itemIndex])
            if (scrollState is InfiniteScrollState.Loading && itemIndex == scrollState.pagination.items.size - 1) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingSpinner()
                }
            }
        }
    }
    if (isAtBottom.value && scrollState !is InfiniteScrollState.Loading && scrollState.pagination.info.nextPage != null) {
        loadMore()
    }
}