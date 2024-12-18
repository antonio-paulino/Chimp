package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.ui.components.Errors
import java.util.UUID

@Composable
fun TokenTextField(
    token: TextFieldValue,
    onTokenChange: (TextFieldValue) -> Unit,
    tokenValidation: Either<List<String>, Unit>?,
) {
    val (tokenVisible, onTokenVisibleChange) = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = token,
            onValueChange = {
                onTokenChange(it)
            },
            label = { Text(stringResource(id = R.string.token_label)) },
            visualTransformation = if (tokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    text = if (tokenVisible) stringResource(R.string.hide) else stringResource(R.string.show),
                    modifier = Modifier.clickable { onTokenVisibleChange(!tokenVisible) }.padding(12.dp),
                )
            },
            singleLine = true,
            modifier = Modifier.size(280.dp, 56.dp),
        )
        if (tokenValidation is Failure) {
            Errors(errors = tokenValidation.value)
        }
    }
}

fun validateToken(token: String): Either<List<String>, Unit> {
    val errors = mutableListOf<String>()

    if (token.isEmpty()) {
        errors.add("Token cannot be empty")
    }

    try {
        UUID.fromString(token)
    } catch (e: IllegalArgumentException) {
        errors.add("Invalid token format")
    }

    return if (errors.isEmpty()) {
        Success(Unit)
    } else {
        Failure(errors)
    }
}
