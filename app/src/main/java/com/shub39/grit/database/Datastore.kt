package com.shub39.grit.database

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object Datastore {
    private const val FILE_NAME = "settings.pb"
    private val Context.dataStore by preferencesDataStore(name = FILE_NAME)
    private val THEME = stringPreferencesKey("theme")
    private val CLEAR_PREFERENCES = stringPreferencesKey("Daily")

    fun clearPreferences(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, "clearPreferences: ", it)
        }.map { preferences ->
            preferences[CLEAR_PREFERENCES] ?: "Never"
        }

    suspend fun setClearPreferences(context: Context, clear: String) {
        context.dataStore.edit { settings ->
            settings[CLEAR_PREFERENCES] = clear
        }
    }

    fun getTheme(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, "getTheme: ", it)
        }.map { preferences ->
            preferences[THEME] ?: "Default"
        }

    suspend fun setTheme(context: Context, theme: String) {
        context.dataStore.edit { settings ->
            settings[THEME] = theme
        }
    }
}