package com.easybraille.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {

    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "app_theme"

    // 0 = Claro, 1 = Oscuro
    private val _currentTheme = MutableStateFlow(0) // Default: claro
    val currentTheme = _currentTheme.asStateFlow()

    private lateinit var prefs: android.content.SharedPreferences

    // Inicializa el ThemeManager (debes llamarlo una vez en MainActivity)
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getInt(KEY_THEME, 0) // Valor por defecto: claro
        _currentTheme.value = saved
    }

    // Cambiar tema y guardarlo
    fun setTheme(theme: Int) {
        _currentTheme.value = theme
        prefs.edit().putInt(KEY_THEME, theme).apply()
    }

    // Retorna si está en modo oscuro
    fun isDarkTheme(): Boolean {
        return _currentTheme.value == 1
    }
}
