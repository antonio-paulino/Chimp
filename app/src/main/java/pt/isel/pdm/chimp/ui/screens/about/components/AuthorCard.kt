package pt.isel.pdm.chimp.ui.screens.about.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import pt.isel.pdm.chimp.R

/**
 * Shows information about the author.
 *
 * @param modifier Modifier to be applied to the card.
 * @param author Author information.
 * @param onSendMail Function to send an email.
 * @param onOpenUrl Function to open a URL.
 * @see Author
 */
@Composable
fun AuthorCard(
    modifier: Modifier = Modifier,
    author: Author,
    onSendMail: (String) -> Unit = {},
    onOpenUrl: (Uri) -> Unit = {},
) {
    Card(
        border = BorderStroke(Dp.Hairline, Color.Gray),
        colors =
            CardColors(
                containerColor = colorResource(R.color.white),
                contentColor = colorResource(R.color.black),
                disabledContentColor = colorResource(R.color.black),
                disabledContainerColor = colorResource(R.color.white),
            ),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthorCardHeader(author.name, author.number, painterResource(id = author.image))
            AuthorCardLinks(
                linkedInUrl = author.socials.linkedInUrl?.let { stringResource(it) },
                githubUrl = author.socials.githubUrl?.let { stringResource(it) },
                mailUrl = author.socials.mailUrl?.let { stringResource(it) },
                onSendMail = onSendMail,
                onOpenUrl = onOpenUrl,
            )
        }
    }
}
