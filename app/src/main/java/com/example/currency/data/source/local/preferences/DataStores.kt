package com.example.currency.data.source.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Datastore to store data
 */
class DataStores(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "currency_prefs")
        private val LAST_UPDATE_TIME_KEY = longPreferencesKey("last_update_time")
    }

    /** Get last update time of database **/
    val lastUpdateTime: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_UPDATE_TIME_KEY] ?: 0L
        }

    /** Set last update time of database **/
    suspend fun setLastUpdateTime(timeMillis: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_UPDATE_TIME_KEY] = timeMillis
        }
    }
}