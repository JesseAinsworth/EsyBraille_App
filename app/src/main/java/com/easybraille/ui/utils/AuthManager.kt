package com.easybraille.ui.utils

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREFS_NAME = "AuthPrefs"
    private const val IS_LOGGED_IN = "isLoggedIn"
    private const val USER_NAME = "userName"
    private const val USER_EMAIL = "userEmail"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Guarda el estado de sesión
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        getPreferences(context).edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
    }

    // Verifica si hay sesión iniciada
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(IS_LOGGED_IN, false)
    }

    // Guarda los datos del usuario
    fun saveUserData(context: Context, name: String, email: String) {
        val editor = getPreferences(context).edit()
        editor.putString(USER_NAME, name)
        editor.putString(USER_EMAIL, email)
        editor.apply()
    }

    // Obtiene el nombre guardado
    fun getUserName(context: Context): String {
        return getPreferences(context).getString(USER_NAME, null) ?: "Usuario"
    }

    // Obtiene el correo guardado
    fun getUserEmail(context: Context): String {
        return getPreferences(context).getString(USER_EMAIL, null) ?: "correo@ejemplo.com"
    }
}
