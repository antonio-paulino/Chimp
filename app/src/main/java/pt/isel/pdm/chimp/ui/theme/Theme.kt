package pt.isel.pdm.chimp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        background = Gray900,
        primary = Brand50,
        error = Red800,
        onError = Red50,
        onErrorContainer = Red900,
        surface = Gray800,
        onSurface = Gray800,
        outline = Gray700,
        onBackground = Gray50,
    )

private val LightColorScheme =
    lightColorScheme(
        background = Gray900,
        primary = Brand300,
        error = Red400,
        onError = Red900,
        onErrorContainer = Red50,
        surface = Gray50,
        onSurface = Gray300,
        outline = Gray300,
        onBackground = Gray900,
    )

@Composable
fun ChIMPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = { RadialGradientBackground { content() } },
    )
}
