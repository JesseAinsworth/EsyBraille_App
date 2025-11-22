package com.easybraille.ui.theme

import androidx.compose.ui.graphics.Color
import com.easybraille.utils.ThemeManager

// --------------------
// OSCURO
// --------------------
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF1C1C1C)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFB3B3B3)

// --------------------
// CLARO
// --------------------
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF5F5F5)
val LightTextPrimary = Color(0xFF000000)
val LightTextSecondary = Color(0xFF555555)

// --------------------
// ACCENTOS (igual)
// --------------------
val BlueAccent = Color(0xFF2196F3)
val GreenAccent = Color(0xFF4CAF50)

// ---------------------------------------------------------
// ALIAS GLOBALES — Estos son los nombres que tu app usa
// ---------------------------------------------------------

// Se usarán automáticamente por el tema seleccionado
val TextPrimary: Color
    get() = if (ThemeManager.isDarkTheme()) DarkTextPrimary else LightTextPrimary

val TextSecondary: Color
    get() = if (ThemeManager.isDarkTheme()) DarkTextSecondary else LightTextSecondary

val CardBackground: Color
    get() = if (ThemeManager.isDarkTheme()) DarkSurface else LightSurface

val AppBackground: Color
    get() = if (ThemeManager.isDarkTheme()) DarkBackground else LightBackground
