package com.kavyakanaja.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = DeepGreen,
    onPrimary = Color.White,
    secondary = Saffron,
    onSecondary = Color.White,
    background = Cream,
    onBackground = DarkText,
    surface = ParchmentLight,
    onSurface = DarkText,
    tertiary = Gold
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4CAF82),
    onPrimary = Color.Black,
    secondary = Saffron,
    onSecondary = Color.Black,
    background = Color(0xFF1A1510),
    onBackground = Color(0xFFF0E8D5),
    surface = ParchmentDark,
    onSurface = Color(0xFFF0E8D5),
    tertiary = Gold
)

@Composable
fun KavyaKanajaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}