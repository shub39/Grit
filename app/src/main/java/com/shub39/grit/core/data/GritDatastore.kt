package com.shub39.grit.core.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val FILE_NAME = "settings.pb"

class GritDatastore(
    private val context: Context
) {

    private val Context.dataStore by preferencesDataStore(name = FILE_NAME)
    private val clearPreference = stringPreferencesKey("clear_preference")

    fun clearPreferences(): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, "clearPreferences: ", it)
        }.map { preferences ->
            preferences[clearPreference] ?: "Never"
        }

    suspend fun setClearPreferences(clear: String) {
        context.dataStore.edit { settings ->
            settings[clearPreference] = clear
        }
    }

}