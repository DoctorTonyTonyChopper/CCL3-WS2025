package at.ustp.dolap.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gray100,
    onPrimary = Black,

    secondary = Gray300,
    onSecondary = Black,

    tertiary = Gray200,
    onTertiary = Black,

    background = Black,
    onBackground = Gray100,

    surface = Gray900,
    onSurface = Gray100,

    surfaceVariant = Gray800,
    onSurfaceVariant = Gray200,

    outline = Gray600
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,

    secondary = Gray800,
    onSecondary = White,

    tertiary = Gray700,
    onTertiary = White,

    background = Gray100,
    onBackground = Black,

    surface = White,
    onSurface = Black,

    surfaceVariant = Gray100,
    onSurfaceVariant = Gray800,

    outline = Gray500
)

@Composable
fun DolapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
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
        content = content
    )
}
