package pt.isel.pdm.chimp.ui.screens.credentials.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.domain.wrappers.email.EmailValidationError
import pt.isel.pdm.chimp.domain.wrappers.email.EmailValidator
import pt.isel.pdm.chimp.domain.wrappers.name.NameValidationError
import pt.isel.pdm.chimp.domain.wrappers.name.NameValidator
import pt.isel.pdm.chimp.domain.wrappers.password.PasswordValidationError
import pt.isel.pdm.chimp.domain.wrappers.password.PasswordValidator
import pt.isel.pdm.chimp.ui.components.inputs.EmailTextField
import pt.isel.pdm.chimp.ui.components.inputs.NameTextField
import pt.isel.pdm.chimp.ui.components.inputs.PasswordTextField
import pt.isel.pdm.chimp.ui.components.inputs.TokenTextField
import pt.isel.pdm.chimp.ui.components.inputs.validateToken
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

const val REGISTER_VIEW_TEST_TAG = "register_view"
const val REGISTER_BUTTON_TEST_TAG = "register_button"
const val NAVIGATE_TO_LOGIN_TEST_TAG = "navigate_to_login"

@Composable
fun RegisterView(
    usernameInitialValue: String = "",
    emailInitialValue: String = "",
    passwordInitialValue: String = "",
    onLoginClick: () -> Unit,
    onRegister: (email: String, username: String, password: String, token: String) -> Unit,
) {
    val (emailValidation, setEmailValidation) = remember { mutableStateOf<Either<List<EmailValidationError>, Unit>>(success(Unit)) }
    val (usernameValidation, setUsernameValidation) = remember { mutableStateOf<Either<List<NameValidationError>, Unit>>(success(Unit)) }
    val (passwordValidation, setPasswordValidation) =
        remember {
            mutableStateOf<Either<List<PasswordValidationError>, Unit>>(
                success(Unit),
            )
        }
    val (tokenValidation, setTokenValidation) = remember { mutableStateOf<Either<List<String>, Unit>>(success(Unit)) }

    val (email, setEmail) = remember { mutableStateOf(TextFieldValue(emailInitialValue)) }
    val (username, setUsername) = remember { mutableStateOf(TextFieldValue(usernameInitialValue)) }
    val (password, setPassword) = remember { mutableStateOf(TextFieldValue(passwordInitialValue)) }
    val (token, setToken) = remember { mutableStateOf(TextFieldValue("")) }

    val passwordValidator = PasswordValidator()
    val emailValidator = EmailValidator()
    val usernameValidator = NameValidator()

    ChIMPTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp).testTag(REGISTER_VIEW_TEST_TAG),
        ) {
            Text(
                text = stringResource(id = R.string.register),
                style = MaterialTheme.typography.titleMedium,
            )
            EmailTextField(
                email = email,
                onEmailChange = {
                    setEmail(it)
                    setEmailValidation(emailValidator.validate(it.text))
                },
                emailValidation = emailValidation,
            )
            NameTextField(
                name = username,
                onNameChange = {
                    setUsername(it)
                    setUsernameValidation(usernameValidator.validate(it.text))
                },
                nameValidation = usernameValidation,
            )
            PasswordTextField(
                password = password,
                onPasswordChange = {
                    setPassword(it.copy(text = it.text.replace("\\s".toRegex(), "")))
                    setPasswordValidation(passwordValidator.validate(it.text))
                },
                passwordValidation = passwordValidation,
            )
            TokenTextField(
                token = token,
                onTokenChange = {
                    setToken(it)
                    setTokenValidation(validateToken(it.text))
                },
                tokenValidation = tokenValidation,
            )
            Text(
                text = stringResource(id = R.string.already_have_an_account),
                modifier = Modifier.clickable { onLoginClick() }.testTag(NAVIGATE_TO_LOGIN_TEST_TAG),
                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
            )
            Button(
                onClick = {
                    onRegister(email.text.trim(), username.text.trim(), password.text.trim(), token.text.trim())
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).testTag(REGISTER_BUTTON_TEST_TAG),
                enabled =
                    listOf(emailValidation, usernameValidation, passwordValidation, tokenValidation).all { it is Success } &&
                        listOf(email, username, password, token).all { it.text.isNotBlank() },
            ) {
                Text(stringResource(id = R.string.register))
            }
        }
    }
}

@Composable
@Preview
fun RegisterViewPreview() {
    RegisterView(onLoginClick = {}, onRegister = { _, _, _, _ -> })
}
