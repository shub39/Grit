package com.shub39.grit.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.materialkolor.PaletteStyle
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.Pages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek

class DataStoreImpl(
    private val datastore: DataStore<Preferences>
) : GritDatastore {

    companion object {
        private val isDarkThemeKey = stringPreferencesKey("use_dark_theme")
        private val seedColorKey = intPreferencesKey("seed_color")
        private val amoledKey = booleanPreferencesKey("amoled")
        private val paletteKey = stringPreferencesKey("palette")
        private val startOfWeekKey = stringPreferencesKey("start_of_week")
        private val startingPageKey = stringPreferencesKey("starting_page")
        private val is24HrKey = booleanPreferencesKey("is_24Hr")
    }

    override fun getDarkThemePref(): Flow<Boolean?> = datastore.data.map { prefs ->
        when (prefs[isDarkThemeKey]) {
            "true" -> true
            "false" -> false
            else -> null
        }
    }
    override suspend fun setDarkThemePref(pref: Boolean?) {
        datastore.edit { prefs ->
            prefs[isDarkThemeKey] = pref.toString()
        }
    }

    override fun getSeedColorFlow(): Flow<Int> = datastore.data.map { prefs ->
        prefs[seedColorKey] ?: Color.White.toArgb()
    }
    override suspend fun setSeedColor(color: Int) {
        datastore.edit { prefs ->
            prefs[seedColorKey] = color
        }
    }

    override fun getAmoledPref(): Flow<Boolean> = datastore.data.map { prefs ->
        prefs[amoledKey] ?: false
    }
    override suspend fun setAmoledPref(pref: Boolean) {
        datastore.edit { prefs ->
            prefs[amoledKey] = pref
        }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> = datastore.data.map { prefs ->
        val style = prefs[paletteKey] ?: PaletteStyle.TonalSpot.name
        return@map PaletteStyle.valueOf(style)
    }
    override suspend fun setPaletteStyle(style: PaletteStyle) {
        datastore.edit { prefs ->
            prefs[paletteKey] = style.name
        }
    }

    override fun getStartOfTheWeekPref(): Flow<DayOfWeek> = datastore.data.map { prefs ->
        val dayOfWeek = prefs[startOfWeekKey] ?: DayOfWeek.MONDAY.name
        return@map DayOfWeek.valueOf(dayOfWeek)
    }
    override suspend fun setStartOfWeek(day: DayOfWeek) {
        datastore.edit { prefs ->
            prefs[startOfWeekKey] = day.name
        }
    }

    override fun getStartingPagePref(): Flow<Pages> = datastore.data.map { pref ->
        val page = pref[startingPageKey] ?: Pages.Tasks.name
        return@map Pages.valueOf(page)
    }
    override suspend fun setStartingPage(page: Pages) {
        datastore.edit { prefs ->
            prefs[startingPageKey] = page.name
        }
    }

    override fun getIs24Hr(): Flow<Boolean> = datastore.data.map { prefs ->
        prefs[is24HrKey] ?: false
    }
    override suspend fun setIs24Hr(pref: Boolean) {
        datastore.edit { prefs ->
            prefs[is24HrKey] = pref
        }
    }
}