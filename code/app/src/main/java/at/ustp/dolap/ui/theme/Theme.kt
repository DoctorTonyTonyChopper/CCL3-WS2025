package at.ustp.dolap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MintPrimaryDark,
    onPrimary = Color(0xFF003733),
    primaryContainer = MintPrimaryContainerDark,
    onPrimaryContainer = MintPrimaryContainer,

    secondary = BerrySecondaryDark,
    onSecondary = Color(0xFF4A0024),
    secondaryContainer = BerrySecondaryContainerDark,
    onSecondaryContainer = BerrySecondaryContainer,

    tertiary = OrangeTertiaryDark,
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = OrangeTertiaryContainerDark,
    onTertiaryContainer = OrangeTertiaryContainer,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = MintPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = MintPrimaryContainer,
    onPrimaryContainer = Color(0xFF003733),

    secondary = BerrySecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = BerrySecondaryContainer,
    onSecondaryContainer = Color(0xFF3F001C),

    tertiary = OrangeTertiary,
    onTertiary = Color(0xFF2B1600),
    tertiaryContainer = OrangeTertiaryContainer,
    onTertiaryContainer = Color(0xFF4A2800),

    background = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,

    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

@Composable
fun DolapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Fixed  colors for a consistent Dolap identity.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}