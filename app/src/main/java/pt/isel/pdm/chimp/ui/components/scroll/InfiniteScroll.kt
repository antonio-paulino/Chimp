package pt.isel.pdm.chimp.ui.components.scroll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.screens.shared.viewModels.InfiniteScrollState
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@Composable
fun <T : Identifiable> InfiniteScroll(
    scrollState: InfiniteScrollState<T>,
    onBottomScroll: () -> Unit,
    reverse: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    itemSpacing: PaddingValues = PaddingValues(0.dp),
    filterCondition: (T) -> Boolean = { true },
    modifier: Modifier = Modifier,
    renderItem: @Composable (T) -> Unit,
) {
    val listState = rememberLazyListState()

    ChIMPTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
        ) { innerPadding ->
            val filteredItems = scrollState.pagination.items.filter(filterCondition)
            if (scrollState is InfiniteScrollState.Loading && filteredItems.isEmpty()) {
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
                contentPadding = if (contentPadding != PaddingValues(0.dp)) contentPadding else innerPadding,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                items(
                    count = filteredItems.size,
                    key = { index -> filteredItems[index].id.value },
                ) { itemIndex ->
                    renderItem(filteredItems[itemIndex])
                    Spacer(modifier = Modifier.padding(itemSpacing))
                    if (itemIndex == filteredItems.size - 1) {
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
    }

    val isAtBottom =
        remember {
            derivedStateOf {
                listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size + 10 >= listState.layoutInfo.totalItemsCount
            }
        }
    val itemCount = scrollState.pagination.items.size
    val previousItemCount = remember { mutableIntStateOf(0) }

    LaunchedEffect(itemCount) {
        if (
            itemCount == previousItemCount.intValue + 1 &&
            (reverse && listState.firstVisibleItemIndex < listState.layoutInfo.visibleItemsInfo.size || !reverse && isAtBottom.value)
        ) {
            listState.animateScrollToItem(if (reverse) 0 else itemCount - 1)
        }
        previousItemCount.intValue = itemCount
    }

    if (isAtBottom.value && scrollState !is InfiniteScrollState.Loading && scrollState.pagination.info.nextPage != null) {
        onBottomScroll()
    }
}
