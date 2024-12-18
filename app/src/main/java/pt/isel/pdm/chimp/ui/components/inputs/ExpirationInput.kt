package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpirationInput(
    expiration: ExpirationOptions,
    setExpiration: (ExpirationOptions) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val expirationOptions = ExpirationOptions.entries.toTypedArray()
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        },
    ) {
        OutlinedTextField(
            value = expiration.toStringResourceRepresentation(),
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.expiration_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            modifier =
            Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .clickable { expanded.value = !expanded.value },
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            expirationOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toStringResourceRepresentation()) },
                    onClick = {
                        setExpiration(option)
                        expanded.value = false
                    },
                )
            }
        }
    }
}

enum class ExpirationOptions(val expirationDate: LocalDateTime) {
    THIRTY_MINUTES(LocalDateTime.now().plusMinutes(30)),
    ONE_HOUR(LocalDateTime.now().plusHours(1)),
    ONE_DAY(LocalDateTime.now().plusDays(1)),
    ONE_WEEK(LocalDateTime.now().plusDays(7)),
    THIRTY_DAYS(LocalDateTime.now().plusDays(30));

    @Composable
    fun toStringResourceRepresentation(): String {
        return when (this) {
            THIRTY_MINUTES -> "30 " + stringResource(R.string.minutes)
            ONE_HOUR -> "1 " + stringResource(R.string.hour)
            ONE_DAY -> "1 " + stringResource(R.string.day)
            ONE_WEEK -> "7 " + stringResource(R.string.days)
            THIRTY_DAYS -> "30 " + stringResource(R.string.days)
        }
    }
}
