package pt.isel.pdm.chimp.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

private const val SMALL_ICON_SIZE = 24
private const val MEDIUM_ICON_SIZE = 30
private const val LARGE_ICON_SIZE = 36

/**
 * Shows a social link.
 *
 * @param icon Icon of the social network.
 * @param backgroundColor Background color of the social network.
 * @param url URL to the social network.
 * @param size Size of the social link.
 */
@Composable
fun SocialLink(
    icon: Int,
    backgroundColor: Color,
    url: String, size:
    SocialLinkSize = SocialLinkSize.MEDIUM,
    onOpenUrl: (Uri) -> Unit = {},
    onSendMail: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clickable {
                if (url.startsWith("mailto:")) {
                    onSendMail(url)
                } else {
                    onOpenUrl(Uri.parse(url))
                }
            }
            .clip(shape = CircleShape)
            .size(size.value.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = icon),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null,
            modifier = Modifier
                .size(size.value.dp - size.value.dp / 3)
        )
    }
}

enum class SocialLinkSize(val value: Int) {
    SMALL(SMALL_ICON_SIZE),
    MEDIUM(MEDIUM_ICON_SIZE),
    LARGE(LARGE_ICON_SIZE)
}