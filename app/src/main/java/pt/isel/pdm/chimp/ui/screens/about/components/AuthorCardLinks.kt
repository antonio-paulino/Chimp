package pt.isel.pdm.chimp.ui.screens.about.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.components.SocialLink
import pt.isel.pdm.chimp.ui.components.SocialLinkSize

private val AUTHOR_CARD_LINKS_SPACING = 10.dp
private val AUTHOR_CARD_LINKS_BOTTOM_PADDING = 16.dp

@Composable
fun AuthorCardLinks(
    linkedInUrl: String?,
    githubUrl: String?,
    mailUrl: String?,
    modifier: Modifier = Modifier,
    onOpenUrl: (Uri) -> Unit,
    onSendMail: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AUTHOR_CARD_LINKS_SPACING),
        modifier = modifier.padding(bottom = AUTHOR_CARD_LINKS_BOTTOM_PADDING),
    ) {
        if (linkedInUrl != null) {
            SocialLink(
                icon = R.drawable.linkedin_in_brands_solid,
                backgroundColor = colorResource(R.color.linkedin),
                url = linkedInUrl,
                size = SocialLinkSize.LARGE,
                onOpenUrl = onOpenUrl,
                onSendMail = onSendMail,
            )
        }
        if (githubUrl != null) {
            SocialLink(
                icon = R.drawable.github_brands_solid,
                backgroundColor = colorResource(R.color.github),
                url = githubUrl,
                size = SocialLinkSize.LARGE,
                onOpenUrl = onOpenUrl,
                onSendMail = onSendMail,
            )
        }
        if (mailUrl != null) {
            SocialLink(
                icon = R.drawable.envelope_solid,
                backgroundColor = colorResource(R.color.mail),
                url = mailUrl,
                size = SocialLinkSize.LARGE,
                onOpenUrl = onOpenUrl,
                onSendMail = onSendMail,
            )
        }
    }
}
