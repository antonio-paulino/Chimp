package pt.isel.pdm.chimp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

const val ERRORS_TEST_TAG = "errors"

@Composable
fun Errors(errors: List<String>) {
    Column(
        modifier = Modifier.testTag(ERRORS_TEST_TAG),
    ) {
        errors.forEach {
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 20.dp),
                text = "• $it",
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
