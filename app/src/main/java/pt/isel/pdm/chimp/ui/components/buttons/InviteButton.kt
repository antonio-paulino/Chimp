package pt.isel.pdm.chimp.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun InviteButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Invite",
                tint = if (enabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
            )
        },
    )
}
