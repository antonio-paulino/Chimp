package pt.isel.pdm.chimp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val STROKE_WIDTH = 4

@Composable
fun LoadingSpinner(text: String? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.background(Color.Transparent),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.background(Color.Transparent),
            strokeWidth = STROKE_WIDTH.dp,
        )
        text?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
            )
        }
    }
}

@Preview
@Composable
fun LoadingSpinnerPreview() {
    LoadingSpinner()
}
