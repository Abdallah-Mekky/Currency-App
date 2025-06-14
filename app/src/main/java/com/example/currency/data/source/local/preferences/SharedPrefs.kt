package com.example.currency.data.source.local.preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "currency_prefs",
        Context.MODE_PRIVATE
    )

    fun getLastUpdate(): Long {
        return prefs.getLong("last_update_time", 0L)
    }

    fun setLastUpdate(timeMillis: Long) {
        prefs.edit().putLong("last_update_time", timeMillis).apply()
    }
}