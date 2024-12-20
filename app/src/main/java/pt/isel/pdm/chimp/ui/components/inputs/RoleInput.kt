package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
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
import pt.isel.pdm.chimp.domain.channel.ChannelRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleInput(
    role: ChannelRole,
    onRoleChange: (ChannelRole) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val expanded = remember { mutableStateOf(false) }
    val roles = ChannelRole.entries.filter { it != ChannelRole.OWNER }
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = role.toStringResourceRepresentation(),
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.role_label)) },
            enabled = enabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            modifier =
                Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .clickable {
                        expanded.value = !expanded.value
                    }
                    .fillMaxWidth(0.8f),
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            roles.forEach { channelRole ->
                DropdownMenuItem(
                    text = {
                        Text(channelRole.toStringResourceRepresentation())
                    },
                    onClick = {
                        onRoleChange(channelRole)
                        expanded.value = false
                    },
                )
            }
        }
    }
}
