package pt.isel.pdm.chimp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Brand50 = Color.hsl(210f, 1.00f, 0.95f)
val Brand100 = Color.hsl(210f, 1.00f, 0.92f)
val Brand200 = Color.hsl(210f, 1.00f, 0.80f)
val Brand300 = Color.hsl(210f, 1.00f, 0.65f)
val Brand400 = Color.hsl(210f, 0.98f, 0.48f)
val Brand500 = Color.hsl(210f, 0.98f, 0.42f)
val Brand600 = Color.hsl(210f, 0.98f, 0.55f)
val Brand700 = Color.hsl(210f, 1.00f, 0.35f)
val Brand800 = Color.hsl(210f, 1.00f, 0.16f)
val Brand900 = Color.hsl(210f, 1.00f, 0.21f)

val Orange50 = Color.hsl(45f, 1.00f, 0.97f)
val Orange100 = Color.hsl(45f, 0.92f, 0.90f)
val Orange200 = Color.hsl(45f, 0.94f, 0.80f)
val Orange300 = Color.hsl(45f, 0.90f, 0.65f)
val Orange400 = Color.hsl(45f, 0.90f, 0.40f)
val Orange500 = Color.hsl(45f, 0.90f, 0.35f)
val Orange600 = Color.hsl(45f, 0.91f, 0.25f)
val Orange700 = Color.hsl(45f, 0.94f, 0.20f)
val Orange800 = Color.hsl(45f, 0.95f, 0.16f)
val Orange900 = Color.hsl(45f, 0.93f, 0.12f)

val Red50 = Color.hsl(0f, 1.00f, 0.97f)
val Red100 = Color.hsl(0f, 0.92f, 0.90f)
val Red200 = Color.hsl(0f, 0.94f, 0.80f)
val Red300 = Color.hsl(0f, 0.90f, 0.65f)
val Red400 = Color.hsl(0f, 0.90f, 0.40f)
val Red500 = Color.hsl(0f, 0.90f, 0.30f)
val Red600 = Color.hsl(0f, 0.91f, 0.25f)
val Red700 = Color.hsl(0f, 0.94f, 0.18f)
val Red800 = Color.hsl(0f, 0.95f, 0.12f)
val Red900 = Color.hsl(0f, 0.93f, 0.06f)

val Gray50 = Color.hsl(220f, 0.35f, 0.97f)
val Gray100 = Color.hsl(220f, 0.30f, 0.94f)
val Gray200 = Color.hsl(220f, 0.20f, 0.88f)
val Gray300 = Color.hsl(220f, 0.20f, 0.80f)
val Gray400 = Color.hsl(220f, 0.20f, 0.65f)
val Gray500 = Color.hsl(220f, 0.20f, 0.42f)
val Gray600 = Color.hsl(220f, 0.20f, 0.35f)
val Gray700 = Color.hsl(220f, 0.20f, 0.25f)
val Gray800 = Color.hsl(220f, 0.30f, 0.06f)
val Gray900 = Color.hsl(220f, 0.35f, 0.03f)

val Green50 = Color.hsl(120f, 0.80f, 0.98f)
val Green100 = Color.hsl(120f, 0.75f, 0.94f)
val Green200 = Color.hsl(120f, 0.75f, 0.87f)
val Green300 = Color.hsl(120f, 0.61f, 0.77f)
val Green400 = Color.hsl(120f, 0.44f, 0.53f)
val Green500 = Color.hsl(120f, 0.59f, 0.30f)
val Green600 = Color.hsl(120f, 0.70f, 0.25f)
val Green700 = Color.hsl(120f, 0.75f, 0.16f)
val Green800 = Color.hsl(120f, 0.84f, 0.10f)
val Green900 = Color.hsl(120f, 0.87f, 0.06f)

@Composable
fun RadialGradientBackground(children: @Composable () -> Unit) {
    val gradient =
        if (isSystemInDarkTheme()) {
            Brush.radialGradient(
                colors =
                    listOf(
                        Color.hsl(210f, 1f, 0.16f, 0.5f),
                        Color.hsl(220f, 0.30f, 0.05f),
                    ),
                center = androidx.compose.ui.geometry.Offset(150f, 350f),
                radius = 1500f,
            )
        } else {
            Brush.radialGradient(
                colors =
                    listOf(
                        Color.hsl(210f, 1f, 0.97f),
                        Color.hsl(0f, 0f, 1f),
                    ),
                center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                radius = 1000f,
            )
        }
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(gradient),
        contentAlignment = Alignment.Center,
    ) {
        children()
    }
}
