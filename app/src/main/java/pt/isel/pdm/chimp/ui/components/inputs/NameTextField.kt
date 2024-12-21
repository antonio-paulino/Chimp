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
import pt.isel.pdm.chimp.domain.wrappers.name.NameValidationError
import pt.isel.pdm.chimp.ui.components.Errors

const val NAME_TEXT_FIELD_TEST_TAG = "name_text_field"

@Composable
fun NameTextField(
    modifier: Modifier = Modifier,
    name: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
    nameValidation: Either<List<NameValidationError>, Unit>? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = name,
            onValueChange = onNameChange,
            isError = nameValidation is Failure,
            modifier = Modifier.testTag(NAME_TEXT_FIELD_TEST_TAG),
            label = { Text(stringResource(id = R.string.name_label)) },
            singleLine = true,
            supportingText = {
                if (nameValidation is Failure) {
                    Errors(errors = nameValidation.value.map { it.toErrorMessage() })
                }
            },
        )
    }
}
