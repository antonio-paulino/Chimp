package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.wrappers.email.EmailValidationError
import pt.isel.pdm.chimp.ui.components.Errors

const val EMAIL_TEXT_FIELD_TEST_TAG = "email_text_field"

@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    email: TextFieldValue,
    onEmailChange: (TextFieldValue) -> Unit,
    emailValidation: Either<List<EmailValidationError>, Unit>? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = email,
            onValueChange = onEmailChange,
            isError = emailValidation is Failure,
            label = { Text(stringResource(R.string.email_label)) },
            singleLine = true,
            supportingText = {
                if (emailValidation is Failure) {
                    Errors(errors = emailValidation.value.map { it.toErrorMessage() })
                }
            },
            modifier = Modifier.testTag(EMAIL_TEXT_FIELD_TEST_TAG),
        )
    }
}
