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
package com.shub39.grit.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.core.theme.AppTheme.DARK
import com.shub39.grit.core.theme.AppTheme.LIGHT
import com.shub39.grit.core.theme.AppTheme.SYSTEM
import com.shub39.grit.core.theme.Theme
import com.shub39.grit.shared.ui.toFontRes
import com.shub39.grit.shared.ui.toMPaletteStyle

@Composable
actual fun GritTheme(theme: Theme, content: @Composable (() -> Unit)) {
    val isDark = when (theme.appTheme) {
        SYSTEM -> isSystemInDarkTheme()
        DARK -> true
        LIGHT -> false
    }

    val colorScheme = rememberDynamicColorScheme(
        seedColor = Color(theme.seedColor),
        isDark = isDark,
        isAmoled = theme.isAmoled,
        style = theme.paletteStyle.toMPaletteStyle(),
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme.animated(),
        motionScheme = MotionScheme.expressive(),
        typography = provideTypography(theme.font.toFontRes()),
        content = content,
    )
}
