package pt.isel.pdm.chimp.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(content: @Composable () -> Unit, actions: @Composable () -> Unit = {}) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(
            scrolledContainerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            content()
        },
        actions = {
            actions()
        }
    )
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(
        content = { Text("Title") },
        actions = { Text("Actions") }
    )
}
