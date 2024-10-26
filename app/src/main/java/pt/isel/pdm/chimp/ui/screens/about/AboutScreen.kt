package pt.isel.pdm.chimp.ui.screens.about

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.screens.about.components.Author
import pt.isel.pdm.chimp.ui.screens.about.components.AuthorCard
import pt.isel.pdm.chimp.ui.screens.about.components.Socials
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onNavIconClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { onNavIconClick() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }
    )
}

@Composable
fun AboutScreen(
    onSendMail: (String) -> Unit = {},
    onOpenUrl: (Uri) -> Unit = {},
    onNavIconClick: () -> Unit = {},
    authors: List<Author>
) {
    ChIMPTheme {
        Scaffold(
            topBar = { TopBar(title = stringResource(R.string.about), onNavIconClick = onNavIconClick) }
        ) { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                for (author in authors) {
                    AuthorCard(
                        author = author,
                        onSendMail = onSendMail,
                        onOpenUrl = onOpenUrl
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun AboutScreenPreview() {
    AboutScreen(
        onSendMail = {},
        onOpenUrl = {},
        onNavIconClick = {},
        authors = listOf(
            Author(
                name = "Bernardo Pereira",
                image = R.drawable.ic_launcher_foreground,
                number = "50493",
                socials = Socials(
                    linkedInUrl = R.string.linkedin_link_bernardo_pereira,
                    githubUrl = R.string.github_link_bernardo_pereira,
                    mailUrl = R.string.mail_link_bernardo_pereira
                )
            )
        )
    )
}

