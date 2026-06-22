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
package com.shub39.grit.widgets

import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.LocalContext
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders as Material3ColorProviders
import com.materialkolor.dynamicColorScheme
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.PaletteStyle
import com.shub39.grit.shared.ui.toMPaletteStyle

/**
 * Builds [ColorProviders] that mirror the app's user-selected theme so that
 * every [androidx.glance.GlanceTheme] call inside a widget reflects the same
 * color palette the user configured in Settings.
 *
 * The logic follows [com.shub39.grit.shared.ui.theme.GritTheme] (Android
 * target) exactly:
 * - If Material You is enabled on Android 12+, use the system dynamic scheme.
 * - Otherwise derive a dynamic scheme from the stored seed color + palette style.
 * - Respect the light/dark/system preference for both light and dark slots.
 */
@Composable
fun rememberWidgetColorProviders(
    appTheme: AppTheme,
    seedColor: Int,
    isAmoled: Boolean,
    paletteStyle: PaletteStyle,
    isMaterialYou: Boolean,
): ColorProviders {
    val context = LocalContext.current

    val lightScheme =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isMaterialYou ->
                try {
                    dynamicLightColorScheme(context)
                } catch (e: Exception) {
                    dynamicColorScheme(
                        seedColor = Color(seedColor),
                        isDark = false,
                        isAmoled = false,
                        style = paletteStyle.toMPaletteStyle(),
                    )
                }

            else ->
                dynamicColorScheme(
                    seedColor = Color(seedColor),
                    isDark = false,
                    isAmoled = false,
                    style = paletteStyle.toMPaletteStyle(),
                )
        }

    val darkScheme =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isMaterialYou ->
                try {
                    dynamicDarkColorScheme(context)
                } catch (e: Exception) {
                    dynamicColorScheme(
                        seedColor = Color(seedColor),
                        isDark = true,
                        isAmoled = isAmoled,
                        style = paletteStyle.toMPaletteStyle(),
                    )
                }

            else ->
                dynamicColorScheme(
                    seedColor = Color(seedColor),
                    isDark = true,
                    isAmoled = isAmoled,
                    style = paletteStyle.toMPaletteStyle(),
                )
        }

    return Material3ColorProviders(light = lightScheme, dark = darkScheme)
}
