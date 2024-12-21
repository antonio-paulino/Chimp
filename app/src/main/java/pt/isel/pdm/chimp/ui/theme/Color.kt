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

val Brand50 = Color(0xFFE6F7FF)
val Brand100 = Color(0xFFEBF4FF)
val Brand200 = Color(0xFFCCE5FF)
val Brand300 = Color(0xFFA6D4FF)
val Brand400 = Color(0xFF7AB8FF)
val Brand500 = Color(0xFF6BA8FF)
val Brand600 = Color(0xFF8CC8FF)
val Brand700 = Color(0xFF5990FF)
val Brand800 = Color(0xFF2940FF)
val Brand900 = Color(0xFF3669FF)

val Orange50 = Color(0xFFFFF7E6)
val Orange100 = Color(0xFFFFE8E6)
val Orange200 = Color(0xFFFFCC99)
val Orange300 = Color(0xFFFFA666)
val Orange400 = Color(0xFFFF6633)
val Orange500 = Color(0xFFFF5933)
val Orange600 = Color(0xFFFF3F33)
val Orange700 = Color(0xFFFF331A)
val Orange800 = Color(0xFFFF291A)
val Orange900 = Color(0xFFFF1F1A)

val Red50 = Color(0xFFFFF7F7)
val Red100 = Color(0xFFFFE8E8)
val Red200 = Color(0xFFFFCCCC)
val Red300 = Color(0xFFFFA6A6)
val Red400 = Color(0xFFFF6666)
val Red500 = Color(0xFFFF4D4D)
val Red600 = Color(0xFFFF3F3F)
val Red700 = Color(0xFFFF2F2F)
val Red800 = Color(0xFFFF1F1F)
val Red900 = Color(0xFFFF0F0F)

val Gray50 = Color(0xFFF7F7F7)
val Gray100 = Color(0xFFF0F0F0)
val Gray200 = Color(0xFFDFDFDF)
val Gray300 = Color(0xFFCCCCCC)
val Gray400 = Color(0xFFA6A6A6)
val Gray500 = Color(0xFF6B6B6B)
val Gray600 = Color(0xFF595959)
val Gray700 = Color(0xFF404040)
val Gray800 = Color(0xFF1A1A1A)
val Gray900 = Color(0xFF0F0F0F)

val Green50 = Color(0xFFF0FFF0)
val Green100 = Color(0xFFE6FFE6)
val Green200 = Color(0xFFDFFFDF)
val Green300 = Color(0xFFBFFF99)
val Green400 = Color(0xFF88FF88)
val Green500 = Color(0xFF4DFF4D)
val Green600 = Color(0xFF3FFF3F)
val Green700 = Color(0xFF2FFF2F)
val Green800 = Color(0xFF1FFF1F)
val Green900 = Color(0xFF0FFF0F)

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
