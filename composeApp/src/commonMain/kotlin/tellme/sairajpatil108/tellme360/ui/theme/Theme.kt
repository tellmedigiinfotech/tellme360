package tellme.sairajpatil108.tellme360.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IOSBlueDark,
    onPrimary = Color.White,
    primaryContainer = IOSSecondaryGroupedBackgroundDark,
    onPrimaryContainer = Color.White,
    secondary = IOSSecondaryGroupedBackgroundDark,
    onSecondary = Color.White,
    tertiary = IOSTertiaryGroupedBackgroundDark,
    onTertiary = Color.White,
    background = IOSGroupedBackgroundDark,
    onBackground = Color(0xFFF2F2F7),
    surface = IOSSecondaryGroupedBackgroundDark,
    onSurface = Color.White,
    surfaceVariant = IOSTertiaryGroupedBackgroundDark,
    onSurfaceVariant = Color(0xFFD1D1D6),
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = IOSBlue,
    onPrimary = Color.White,
    primaryContainer = IOSSecondaryGroupedBackground,
    onPrimaryContainer = Color(0xFF1C1C1E),
    secondary = IOSSecondaryGroupedBackground,
    onSecondary = Color(0xFF1C1C1E),
    tertiary = IOSTertiaryGroupedBackground,
    onTertiary = Color(0xFF1C1C1E),
    background = IOSGroupedBackground,
    onBackground = Color(0xFF1C1C1E),
    surface = IOSSecondaryGroupedBackground,
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = IOSTertiaryGroupedBackground,
    onSurfaceVariant = Color(0xFF6C6C70),
    error = DangerRed,
    onError = Color.White
)

@Composable
fun TellMe360Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

