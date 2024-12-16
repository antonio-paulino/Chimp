package pt.isel.pdm.chimp.ui.screens.about

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.components.NavBar
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.screens.about.components.Author
import pt.isel.pdm.chimp.ui.screens.about.components.AuthorCard
import pt.isel.pdm.chimp.ui.screens.about.components.Socials
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@Composable
fun AboutScreen(
    onSendMail: (String) -> Unit = {},
    onOpenUrl: (Uri) -> Unit = {},
    onHomeNavigation: () -> Unit,
    onSearchNavigation: () -> Unit,
    onInvitationsNavigation: () -> Unit,
    authors: List<Author>,
) {
    Scaffold(
        topBar = {
            TopBar(content = { Text(text = stringResource(R.string.about)) })
        },
        bottomBar = {
            NavBar(
                onHomeNavigation = onHomeNavigation,
                onSearchNavigation = onSearchNavigation,
                onInvitationsNavigation = onInvitationsNavigation,
                onAboutNavigation = {},
                currentScreen = stringResource(R.string.about),
            )
        },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
        ) {
            LazyColumn {
                items(authors.size) { index ->
                    AuthorCard(
                        Modifier.padding(vertical = 6.dp),
                        author = authors[index],
                        onSendMail = onSendMail,
                        onOpenUrl = onOpenUrl,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun AboutScreenPreview() {
    ChIMPTheme {
        AboutScreen(
            onSendMail = {},
            onOpenUrl = {},
            onHomeNavigation = {},
            onSearchNavigation = {},
            onInvitationsNavigation = {},
            authors =
                listOf(
                    Author(
                        name = "Bernardo Pereira",
                        image = R.drawable.ic_launcher_foreground,
                        number = "50493",
                        socials =
                            Socials(
                                linkedInUrl = R.string.linkedin_link_bernardo_pereira,
                                githubUrl = R.string.github_link_bernardo_pereira,
                                mailUrl = R.string.mail_link_bernardo_pereira,
                            ),
                    ),
                ),
        )
    }
}
