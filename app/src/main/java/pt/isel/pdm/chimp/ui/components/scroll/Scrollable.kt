package pt.isel.pdm.chimp.ui.components.scroll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@Composable
fun <T : Identifiable> Scrollable(
    items: List<T>,
    reverse: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    itemSpacing: PaddingValues = PaddingValues(0.dp),
    renderItem: @Composable (T) -> Unit,
) {
    val listState = rememberLazyListState()
    val snackBarHost = remember { SnackbarHostState() }

    ChIMPTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackBarHost) },
            containerColor = Color.Transparent,
        ) { innerPadding ->
            LazyColumn(
                reverseLayout = reverse,
                state = listState,
                contentPadding = if (contentPadding != PaddingValues(0.dp)) contentPadding else innerPadding,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                items(
                    count = items.size,
                    key = { index -> items[index].id.value },
                ) { itemIndex ->
                    renderItem(items[itemIndex])
                    Spacer(modifier = Modifier.padding(itemSpacing))
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

    val previousItemCount = remember { mutableIntStateOf(0) }

    LaunchedEffect(items.size) {
        if (items.size == previousItemCount.intValue + 1 && isAtBottom.value) {
            listState.animateScrollToItem(items.size - 1)
        }
        previousItemCount.intValue = items.size
    }
}
