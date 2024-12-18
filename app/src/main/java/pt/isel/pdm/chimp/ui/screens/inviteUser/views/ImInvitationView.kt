package pt.isel.pdm.chimp.ui.screens.inviteUser.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun ImInvitationView(invite: ImInvitation) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDate = invite.expiresAt.format(formatter)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.invitation_info),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.invitation_token) + " ${invite.token}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                IconButton(
                    onClick = { copyToClipboard(context, clipboardManager, invite.token.toString()) },
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Copy token to clipboard")
                }
            }
            Text(
                text = stringResource(id = R.string.invitation_expires_at) + " $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

fun copyToClipboard(
    context: Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    text: String,
    message: String = "Token copied to clipboard",
) {
    clipboardManager.setText(AnnotatedString(text))
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
@Preview
fun ImInvitationViewPreview() {
    ChIMPTheme {
        ImInvitationView(
            invite =
                ImInvitation(
                    token = UUID.randomUUID(),
                    expiresAt = java.time.LocalDateTime.now(),
                ),
        )
    }
}
