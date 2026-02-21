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
package com.shub39.grit.core.domain

import com.materialkolor.PaletteStyle
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import kotlinx.coroutines.flow.Flow

interface ThemeDatastore {
    suspend fun resetAppTheme()

    fun getAppThemeFlow(): Flow<AppTheme>

    suspend fun setAppTheme(theme: AppTheme)

    fun getSeedColorFlow(): Flow<Int>

    suspend fun setSeedColor(color: Int)

    fun getAmoledPref(): Flow<Boolean>

    suspend fun setAmoledPref(pref: Boolean)

    fun getPaletteStyle(): Flow<PaletteStyle>

    suspend fun setPaletteStyle(style: PaletteStyle)

    fun getMaterialYouFlow(): Flow<Boolean>

    suspend fun setMaterialYou(pref: Boolean)

    fun getFontPrefFlow(): Flow<Fonts>

    suspend fun setFontPref(font: Fonts)
}
