package com.example.savor.data.local

import android.content.Context
import android.content.SharedPreferences

enum class SortOrder { DEFAULT, NAME, NUTRISCORE }

class SettingsStore(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastSearch: String
        get() = prefs.getString(KEY_LAST_SEARCH, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_SEARCH, value).apply()

    var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var sortOrder: SortOrder
        get() = runCatching {
            SortOrder.valueOf(prefs.getString(KEY_SORT, SortOrder.DEFAULT.name)!!)
        }.getOrDefault(SortOrder.DEFAULT)
        set(value) = prefs.edit().putString(KEY_SORT, value.name).apply()

    private companion object {
        const val PREFS_NAME = "savor_prefs"
        const val KEY_LAST_SEARCH = "last_search"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_SORT = "sort_order"
    }
}