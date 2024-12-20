package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R

@Composable
fun ChannelSearchBar(
    searchField: String,
    onSearch: (String) -> Unit,
    doSearch: () -> Unit,
) {
    val (searchFieldValue, setSearchFieldValue) = remember { mutableStateOf(searchField) }

    TextField(
        modifier =
            Modifier
                .fillMaxSize(0.9f),
        value = searchFieldValue,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.labelLarge,
        onValueChange = {
            setSearchFieldValue(it)
            onSearch(it)
        },
        placeholder = { Text(stringResource(id = R.string.search_channels)) },
    )

    LaunchedEffect(searchField) {
        doSearch()
    }
}
