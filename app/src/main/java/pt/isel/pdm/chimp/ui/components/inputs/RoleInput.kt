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
import pt.isel.pdm.chimp.domain.channel.ChannelRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleInput(
    role: ChannelRole,
    setRole: (ChannelRole) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val roles = ChannelRole.entries.filter { it != ChannelRole.OWNER }
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        },
    ) {
        OutlinedTextField(
            value = role.name,
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.role_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            modifier =
                Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .clickable {
                        expanded.value = !expanded.value
                    },
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            roles.forEach { channelRole ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (channelRole) {
                                ChannelRole.MEMBER -> stringResource(R.string.member)
                                ChannelRole.GUEST -> stringResource(R.string.guest)
                                else -> ""
                            },
                        )
                    },
                    onClick = {
                        setRole(channelRole)
                        expanded.value = false
                    },
                )
            }
        }
    }
}
