package pt.isel.pdm.chimp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.components.NavBar
import pt.isel.pdm.chimp.ui.components.TopBar

@Composable
fun MainScreen(onAboutNavigation: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
            )
        },
        bottomBar = {
            NavBar(
                onHomeNavigation = {},
                onSearchNavigation = {},
                onInvitationsNavigation = {},
                onAboutNavigation = onAboutNavigation,
                currentScreen = stringResource(id = R.string.home),
            )
        },
    ) { innerPadding ->
        Text(
            text = "Hello, ChIMP!",
            modifier = Modifier.padding(innerPadding),
        )
    }
}
