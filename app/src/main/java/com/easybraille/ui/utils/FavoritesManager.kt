package com.easybraille.ui.utils

import android.content.Context
import android.content.SharedPreferences

object FavoritesManager {
    private const val PREF_NAME = "pdf_favorites"
    private const val KEY_FAVORITES = "favorite_files"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isFavorite(context: Context, fileName: String): Boolean {
        val favorites = getFavoritesSet(context)
        return favorites.contains(fileName)
    }

    fun toggleFavorite(context: Context, fileName: String) {
        val favorites = getFavoritesSet(context).toMutableSet()
        if (favorites.contains(fileName)) {
            favorites.remove(fileName)
        } else {
            favorites.add(fileName)
        }
        saveFavoritesSet(context, favorites)
    }

    private fun getFavoritesSet(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private fun saveFavoritesSet(context: Context, favorites: Set<String>) {
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
}