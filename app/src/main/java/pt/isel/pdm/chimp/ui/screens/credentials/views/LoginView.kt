package pt.isel.pdm.chimp.ui.screens.credentials.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.components.inputs.PasswordTextField
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.allValid

@Composable
fun LoginView(
    emailOrUsernameInitialValue: String = "",
    passwordInitialValue: String = "",
    onRegisterClick: () -> Unit,
    onLogin: (emailOrUsername: String, password: String) -> Unit,
) {
    val (emailOrUsername, onEmailOrUsernameChange) = remember { mutableStateOf(TextFieldValue(emailOrUsernameInitialValue)) }
    val (password, onPasswordChange) = remember { mutableStateOf(TextFieldValue(passwordInitialValue)) }
    ChIMPTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.login),
                style = MaterialTheme.typography.titleMedium,
            )
            TextField(
                value = emailOrUsername,
                onValueChange = onEmailOrUsernameChange,
                label = { Text(stringResource(id = R.string.email_or_username)) },
                modifier = Modifier.size(280.dp, 56.dp),
                singleLine = true,
            )
            PasswordTextField(
                password = password,
                onPasswordChange = onPasswordChange,
            )
            Text(
                text = stringResource(id = R.string.dont_have_an_account),
                modifier = Modifier.clickable { onRegisterClick() },
                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
            )
            Button(
                onClick = { onLogin(emailOrUsername.text, password.text) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = listOf(emailOrUsername, password).allValid(),
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginViewPreview() {
    LoginView(onRegisterClick = {}, onLogin = { _, _ -> })
}
