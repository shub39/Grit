/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import org.koin.core.annotation.Single

@Single(binds = [GritDatastore::class])
class DataStoreImpl(private val datastore: DataStore<Preferences>) : GritDatastore {

    companion object {
        private val appThemeKey = stringPreferencesKey("app_theme")
        private val seedColorKey = intPreferencesKey("seed_color")
        private val amoledKey = booleanPreferencesKey("amoled")
        private val paletteKey = stringPreferencesKey("palette")
        private val startOfWeekKey = stringPreferencesKey("start_of_week")
        private val startingSectionKey = stringPreferencesKey("starting_page")
        private val is24HrKey = booleanPreferencesKey("is_24Hr")
        private val materialYouKey = booleanPreferencesKey("material_you")
        private val notificationsKey = booleanPreferencesKey("notifications")
        private val fontPrefKey = stringPreferencesKey("font")
        private val biometricLockKey = booleanPreferencesKey("biometric")
        private val taskReorderKey = booleanPreferencesKey("task_reorder")
        private val compactHabitView = booleanPreferencesKey("compact_habit_view")
        private val lastChangelogShownKey = stringPreferencesKey("last_changelog_shown")
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

    override fun getAppThemeFlow(): Flow<AppTheme> =
        datastore.data.map { prefs ->
            val appTheme = prefs[appThemeKey] ?: AppTheme.SYSTEM.name
            AppTheme.valueOf(appTheme)
        }

    override suspend fun setAppTheme(theme: AppTheme) {
        datastore.edit { prefs -> prefs[appThemeKey] = theme.name }
    }

    override fun getSeedColorFlow(): Flow<Int> =
        datastore.data.map { prefs -> prefs[seedColorKey] ?: Color.White.toArgb() }

    override suspend fun setSeedColor(color: Int) {
        datastore.edit { prefs -> prefs[seedColorKey] = color }
    }

    override fun getAmoledPref(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[amoledKey] == true }

    override suspend fun setAmoledPref(pref: Boolean) {
        datastore.edit { prefs -> prefs[amoledKey] = pref }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> =
        datastore.data.map { prefs ->
            val style = prefs[paletteKey] ?: PaletteStyle.TonalSpot.name
            return@map PaletteStyle.valueOf(style)
        }

    override suspend fun setPaletteStyle(style: PaletteStyle) {
        datastore.edit { prefs -> prefs[paletteKey] = style.name }
    }

    override fun getStartOfTheWeekPref(): Flow<DayOfWeek> =
        datastore.data.map { prefs ->
            val dayOfWeek = prefs[startOfWeekKey] ?: DayOfWeek.MONDAY.name
            return@map DayOfWeek.valueOf(dayOfWeek)
        }

    override suspend fun setStartOfWeek(day: DayOfWeek) {
        datastore.edit { prefs -> prefs[startOfWeekKey] = day.name }
    }

    override fun getStartingSectionPref(): Flow<Sections> =
        datastore.data.map { pref ->
            val page = pref[startingSectionKey] ?: Sections.Tasks.name
            return@map Sections.valueOf(page)
        }

    override suspend fun setStartingPage(page: Sections) {
        datastore.edit { prefs -> prefs[startingSectionKey] = page.name }
    }

    override fun getIs24Hr(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[is24HrKey] == true }

    override suspend fun setIs24Hr(pref: Boolean) {
        datastore.edit { prefs -> prefs[is24HrKey] = pref }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[materialYouKey] == true }

    override suspend fun setMaterialYou(pref: Boolean) {
        datastore.edit { prefs -> prefs[materialYouKey] = pref }
    }

    override fun getNotificationsFlow(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[notificationsKey] == true }

    override suspend fun setNotifications(pref: Boolean) {
        datastore.edit { prefs -> prefs[notificationsKey] = pref }
    }

    override fun getFontPrefFlow(): Flow<Fonts> =
        datastore.data.map { prefs ->
            val font = prefs[fontPrefKey] ?: Fonts.FIGTREE.name
            Fonts.valueOf(font)
        }

    override suspend fun setFontPref(font: Fonts) {
        datastore.edit { prefs -> prefs[fontPrefKey] = font.name }
    }

    override fun getBiometricLockPref(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[biometricLockKey] == true }

    override suspend fun setBiometricPref(pref: Boolean) {
        datastore.edit { prefs -> prefs[biometricLockKey] = pref }
    }

    override fun getTaskReorderPref(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[taskReorderKey] ?: true }

    override suspend fun setTaskReorderPref(pref: Boolean) {
        datastore.edit { prefs -> prefs[taskReorderKey] = pref }
    }

    override fun getCompactViewPref(): Flow<Boolean> =
        datastore.data.map { pref -> pref[compactHabitView] ?: false }

    override suspend fun setCompactView(pref: Boolean) {
        datastore.edit { prefs -> prefs[compactHabitView] = pref }
    }

    override fun getLastChangelogShown(): Flow<String> =
        datastore.data.map { prefs -> prefs[lastChangelogShownKey] ?: "" }

    override suspend fun updateLastChangelogShown(version: String) {
        datastore.edit { settings -> settings[lastChangelogShownKey] = version }
    }
}
