package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.wrappers.password.PasswordValidationError
import pt.isel.pdm.chimp.ui.components.Errors

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: TextFieldValue,
    onPasswordChange: (TextFieldValue) -> Unit,
    passwordValidation: Either<List<PasswordValidationError>, Unit>? = null,
) {
    val (passwordVisible, onPasswordVisibleChange) = remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
            },
            label = { Text(stringResource(R.string.password_label)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordValidation is Failure,
            trailingIcon = {
                Text(
                    text = if (passwordVisible) stringResource(R.string.hide) else stringResource(R.string.show),
                    modifier =
                        Modifier.clickable { onPasswordVisibleChange(!passwordVisible) }
                            .padding(12.dp),
                )
            },
            supportingText = {
                if (passwordValidation is Failure) {
                    Errors(errors = passwordValidation.value.map { it.toErrorMessage() })
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
        )
    }
}
