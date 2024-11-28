package pt.isel.pdm.chimp.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(content: @Composable () -> Unit) {
    ChIMPTheme {
        TopAppBar(
            title = {
                content()
            },
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar {
        Text(text = "Title")
    }
}
