package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R

@Composable
fun SearchBarField(
    searchField: String,
    onSearch: (String) -> Unit,
    doSearch: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.search_channels),
) {
    val (searchFieldValue, setSearchFieldValue) = remember { mutableStateOf(searchField) }

    Box(
        modifier = modifier.padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.9f),
            value = searchFieldValue,
            shape = RoundedCornerShape(12.dp),
            textStyle =
                MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            onValueChange = {
                setSearchFieldValue(it)
                onSearch(it)
            },
            label = {
                Text(
                    text = label,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        ),
                    maxLines = 1,
                )
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            singleLine = true,
        )
    }

    LaunchedEffect(searchField) {
        doSearch()
    }
}
