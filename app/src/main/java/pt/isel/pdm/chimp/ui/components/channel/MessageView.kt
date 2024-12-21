package pt.isel.pdm.chimp.ui.components.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.messages.Message
import java.time.format.DateTimeFormatter

@Composable
fun MessageView(
    canEdit: Boolean,
    canDelete: Boolean,
    message: Message,
    isAuthor: Boolean,
    onEdit: (Message) -> Unit,
    onDelete: (Message) -> Unit,
) {
    MessageBubble(
        message = message,
        isSentByUser = isAuthor,
        onEdit = onEdit,
        onDelete = onDelete,
        canDelete = canDelete,
        canEdit = canEdit,
    )
}

@Composable
fun MessageBubble(
    message: Message,
    isSentByUser: Boolean,
    onEdit: (Message) -> Unit,
    onDelete: (Message) -> Unit,
    canDelete: Boolean,
    canEdit: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                scope.launch {
                                    expanded = true
                                }
                            },
                        )
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = if (isSentByUser) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(0.48f),
        ) {
            Column {
                if (!isSentByUser) {
                    Text(
                        text = message.author.name.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSentByUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text =
                        if (message.editedAt != null) {
                            "Edited: ${message.editedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}"
                        } else {
                            message.createdAt.format(DateTimeFormatter.ofPattern("HH:mm"))
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        if (isSentByUser) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        },
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                if (canEdit) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit)) },
                        onClick = {
                            onEdit(message)
                            expanded = false
                        },
                    )
                }
                if (canDelete) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            onDelete(message)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
