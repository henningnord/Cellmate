package no.uio.ifi.in2000.cellmate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = primary,  //cellmate yellow
    background = background, //white
    surface = surface,  //offwhite
    error = error,  //red
    onBackground = onBackground,  //black - default on white background
    onSurface = onSurface,  //black - default on offwhite background

)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = primary,
    background = darkBackground,  // dark gray background
    surface = darkSurface,  // slightly lighter surface
    error = darkError,  // dark theme error
    onBackground = Color.White,  // white text on dark background
    onSurface = Color.White,  // white text on dark surfaces
)

@Composable
fun CellMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}