package pt.isel.pdm.chimp.ui.components.inputs

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChannelPrivacyInput(
    isPublic: Boolean,
    setIsPublic: (Boolean) -> Unit,
) {
    Switch(
        checked = !isPublic,
        onCheckedChange = { setIsPublic(!it) },
        thumbContent = {
            if (!isPublic) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Private",
                    Modifier.size(16.dp),
                )
            }
        },
    )
}
