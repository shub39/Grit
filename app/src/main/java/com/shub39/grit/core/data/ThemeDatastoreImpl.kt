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
import com.shub39.grit.core.domain.ThemeDatastore
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [ThemeDatastore::class])
class ThemeDatastoreImpl(private val datastore: DataStore<Preferences>) : ThemeDatastore {
    companion object {
        private val appThemeKey = stringPreferencesKey("app_theme")
        private val seedColorKey = intPreferencesKey("seed_color")
        private val amoledKey = booleanPreferencesKey("amoled")
        private val paletteKey = stringPreferencesKey("palette")
        private val materialYouKey = booleanPreferencesKey("material_you")
        private val fontPrefKey = stringPreferencesKey("font")
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

    override fun getMaterialYouFlow(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[materialYouKey] == true }

    override suspend fun setMaterialYou(pref: Boolean) {
        datastore.edit { prefs -> prefs[materialYouKey] = pref }
    }

    override fun getFontPrefFlow(): Flow<Fonts> =
        datastore.data.map { prefs ->
            val font = prefs[fontPrefKey] ?: Fonts.FIGTREE.name
            Fonts.valueOf(font)
        }

    override suspend fun setFontPref(font: Fonts) {
        datastore.edit { prefs -> prefs[fontPrefKey] = font.name }
    }
}
