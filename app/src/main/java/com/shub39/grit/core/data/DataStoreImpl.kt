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
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.domain.Fonts
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.Pages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek

class DataStoreImpl(
    private val datastore: DataStore<Preferences>
) : GritDatastore {

    companion object {
        private val appThemeKey = stringPreferencesKey("app_theme")
        private val seedColorKey = intPreferencesKey("seed_color")
        private val amoledKey = booleanPreferencesKey("amoled")
        private val paletteKey = stringPreferencesKey("palette")
        private val startOfWeekKey = stringPreferencesKey("start_of_week")
        private val startingPageKey = stringPreferencesKey("starting_page")
        private val is24HrKey = booleanPreferencesKey("is_24Hr")
        private val materialYouKey = booleanPreferencesKey("material_you")
        private val notificationsKey = booleanPreferencesKey("notifications")
        private val fontPrefKey = stringPreferencesKey("font")
        private val biometricLockKey = booleanPreferencesKey("biometric")
    }

    override suspend fun resetAppTheme() {
        datastore.edit { settings ->
            settings[seedColorKey] = Color.White.toArgb()
            settings[amoledKey] = false
            settings[paletteKey] = PaletteStyle.TonalSpot.name
            settings[materialYouKey] = false
            settings[fontPrefKey] = Fonts.FIGTREE.name
        }
    }

    override fun getAppThemeFlow(): Flow<AppTheme> = datastore.data.map { prefs ->
        val appTheme = prefs[appThemeKey] ?: AppTheme.SYSTEM.name
        AppTheme.valueOf(appTheme)
    }

    override suspend fun setAppTheme(theme: AppTheme) {
        datastore.edit { prefs ->
            prefs[appThemeKey] = theme.name
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
        prefs[amoledKey] == true
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
        prefs[is24HrKey] == true
    }

    override suspend fun setIs24Hr(pref: Boolean) {
        datastore.edit { prefs ->
            prefs[is24HrKey] = pref
        }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> = datastore.data.map { prefs ->
        prefs[materialYouKey] == true
    }

    override suspend fun setMaterialYou(pref: Boolean) {
        datastore.edit { prefs ->
            prefs[materialYouKey] = pref
        }
    }

    override fun getNotificationsFlow(): Flow<Boolean> = datastore.data.map { prefs ->
        prefs[notificationsKey] == true
    }

    override suspend fun setNotifications(pref: Boolean) {
        datastore.edit { prefs ->
            prefs[notificationsKey] = pref
        }
    }

    override fun getFontPrefFlow(): Flow<Fonts> = datastore.data.map { prefs ->
        val font = prefs[fontPrefKey] ?: Fonts.FIGTREE.name
        Fonts.valueOf(font)
    }

    override suspend fun setFontPref(font: Fonts) {
        datastore.edit { prefs ->
            prefs[fontPrefKey] = font.name
        }
    }

    override fun getBiometricLockPref(): Flow<Boolean> = datastore.data.map { prefs ->
        prefs[biometricLockKey] == true
    }

    override suspend fun setBiometricPref(pref: Boolean) {
        datastore.edit { prefs ->
            prefs[biometricLockKey] = pref
        }
    }
}