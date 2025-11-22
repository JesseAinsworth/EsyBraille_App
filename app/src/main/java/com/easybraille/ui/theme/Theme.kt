package com.easybraille.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// -------------------------
// PALETA OSCURA
// -------------------------
private val DarkColorScheme = darkColorScheme(
    primary = BlueAccent,
    secondary = GreenAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkTextPrimary,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

// -------------------------
// PALETA CLARA
// -------------------------
private val LightColorScheme = lightColorScheme(
    primary = BlueAccent,
    secondary = GreenAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightTextPrimary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

// -------------------------
// TEMA DE LA APP
// -------------------------
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // 👈 Debe aceptar este parámetro
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

}