package pt.isel.pdm.chimp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.theme.montserratFamily

@Composable
fun NavBar(
    onHomeNavigation: () -> Unit,
    onSearchNavigation: () -> Unit,
    onInvitationsNavigation: () -> Unit,
    onAboutNavigation: () -> Unit,
    currentScreen: String = "Home",
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            NavIcon(
                Icons.Default.Home,
                onHomeNavigation,
                stringResource(R.string.home),
                currentScreen,
            )
            NavIcon(
                Icons.Default.Search,
                onSearchNavigation,
                stringResource(R.string.search),
                currentScreen,
            )
            NavIcon(
                Icons.Default.MailOutline,
                onInvitationsNavigation,
                stringResource(R.string.invitations),
                currentScreen,
            )
            NavIcon(
                Icons.Default.Info,
                onAboutNavigation,
                stringResource(R.string.about),
                currentScreen,
            )
        }
    }
}

@Composable
fun NavIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    text: String,
    currentScreen: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.width(72.dp),
            colors =
                if (currentScreen == text) {
                    IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)
                } else {
                    IconButtonDefaults.iconButtonColors()
                },
        ) {
            Icon(icon, contentDescription = text)
        }
        Text(
            text = text,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = montserratFamily(),
                    fontWeight = FontWeight.Bold,
                ),
        )
    }
}

@Preview
@Composable
fun NavBarPreview() {
    NavBar(
        onHomeNavigation = {},
        onSearchNavigation = {},
        onInvitationsNavigation = {},
        onAboutNavigation = {},
    )
}
