package pt.isel.pdm.chimp.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AcceptButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        content = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Accept",
                tint = if (enabled) Color.Green else Color.Green.copy(alpha = 0.5f),
            )
        },
    )
}
