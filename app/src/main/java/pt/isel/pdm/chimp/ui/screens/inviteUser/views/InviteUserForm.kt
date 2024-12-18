package pt.isel.pdm.chimp.ui.screens.inviteUser.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationInput
import pt.isel.pdm.chimp.ui.components.inputs.ExpirationOptions
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@Composable
fun InviteUserForm(
    expirationOption: ExpirationOptions,
    onCreateInvite: (ExpirationOptions) -> Unit,
    createdInvite: ImInvitation? = null,
) {
    val (expiration, setExpiration) = remember { mutableStateOf(expirationOption) }
    ChIMPTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            ExpirationInput(
                expiration = expiration,
                setExpiration = setExpiration,
            )
            Button(
                onClick = { onCreateInvite(expiration) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(id = R.string.create_channel),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            createdInvite?.let {
                ImInvitationView(invite = it)
            }
        }
    }
}


@Preview
@Composable
fun InviteUserFormPreview() {
    ChIMPTheme {
        InviteUserForm(
            expirationOption = ExpirationOptions.THIRTY_MINUTES,
            onCreateInvite = {},
        )
    }
}