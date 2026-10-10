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
package com.shub39.grit.shared.ui.setting.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.PaletteStyle
import com.shub39.grit.shared.ui.components.ListSelect
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.toMPaletteStyle
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
actual fun MaterialYouToggle(
    isUserSubscribed: Boolean,
    isMaterialYou: Boolean,
    onClick: (Boolean) -> Unit,
) {}

@Composable
actual fun PaletteStylePicker(
    paletteStyle: PaletteStyle,
    isMaterialYou: Boolean,
    seedColor: Color,
    appTheme: AppTheme,
    isAmoled: Boolean,
    isUserSubscribed: Boolean,
    onClick: (PaletteStyle) -> Unit,
) {
    Column(modifier = Modifier.clip(endItemShape())) {
        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.palette_style)) },
            colors = listItemColors(),
            supportingContent = {
                Text(
                    text =
                        paletteStyle.toString().lowercase().replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                )
            },
            leadingContent = {
                Icon(imageVector = vectorResource(Res.drawable.palette), contentDescription = null)
            },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier.fillMaxWidth()
                    .background(listItemColors().containerColor)
                    .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
        ) {
            ListSelect(
                title = null,
                options = PaletteStyle.entries.toList(),
                selected = paletteStyle,
                onSelectedChange = onClick,
                enabled = isUserSubscribed,
                labelProvider = { style: PaletteStyle ->
                    val scheme =
                        rememberDynamicColorScheme(
                            primary = seedColor,
                            isDark =
                                when (appTheme) {
                                    AppTheme.SYSTEM -> isSystemInDarkTheme()
                                    AppTheme.DARK -> true
                                    AppTheme.LIGHT -> false
                                },
                            isAmoled = isAmoled,
                            style = style.toMPaletteStyle(),
                        )

                    Row(
                        modifier =
                            Modifier.size(width = 64.dp, height = 20.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                    ) {
                        listOf(
                                scheme.primary,
                                scheme.primaryContainer,
                                scheme.onPrimary,
                                scheme.secondary,
                                scheme.secondaryContainer,
                                scheme.onSecondary,
                                scheme.tertiary,
                                scheme.tertiaryContainer,
                                scheme.onTertiary,
                                scheme.surface,
                                scheme.onSurface,
                            )
                            .forEach { color ->
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight().background(color)
                                )
                            }
                    }
                },
            )
        }
    }
}
