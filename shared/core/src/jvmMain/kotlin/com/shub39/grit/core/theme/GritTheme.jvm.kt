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
package com.shub39.grit.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.theme.Fonts.Companion.toFontRes

@Composable
actual fun GritTheme(theme: Theme, content: @Composable (() -> Unit)) {
    DynamicMaterialTheme(
        seedColor = theme.seedColor,
        isDark =
            when (theme.appTheme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
            },
        isAmoled = theme.isAmoled,
        style = theme.paletteStyle,
        typography =
            theme.font.toFontRes()?.let { provideTypography(it) } ?: MaterialTheme.typography,
        content = content,
    )
}
