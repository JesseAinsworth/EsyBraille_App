package com.easybraille.ui.utils

import android.content.Context
import android.content.SharedPreferences

object AuthManager {

    private const val PREF_NAME = "easybraille_prefs"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserData(context: Context, id: String, name: String, email: String) {
        prefs(context).edit()
            .putString(KEY_USER_ID, id)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun setLoggedIn(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, value).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false)
    }

    fun getUserId(context: Context): String {
        return prefs(context).getString(KEY_USER_ID, "") ?: ""
    }

    fun getUserName(context: Context): String {
        return prefs(context).getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserEmail(context: Context): String {
        return prefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun logout(context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .apply()
    }
}
