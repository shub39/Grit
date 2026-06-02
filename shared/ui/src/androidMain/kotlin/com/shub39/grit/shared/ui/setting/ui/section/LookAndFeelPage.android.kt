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

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.PaletteStyle
import com.shub39.grit.shared.ui.components.ExpressiveSwitch
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.components.middleItemShape
import com.shub39.grit.shared.ui.toMPaletteStyle
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
actual fun MaterialYouToggle(
    isUserSubscribed: Boolean,
    isMaterialYou: Boolean,
    onClick: (Boolean) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.material_theme)) },
            supportingContent = { Text(text = stringResource(Res.string.material_theme_desc)) },
            trailingContent = {
                ExpressiveSwitch(checked = isMaterialYou, onCheckedChange = onClick)
            },
            colors = listItemColors(),
            modifier = Modifier.clip(if (isUserSubscribed) middleItemShape() else endItemShape()),
        )
    }
}

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

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier.fillMaxWidth()
                    .background(listItemColors().containerColor)
                    .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PaletteStyle.entries.toList().forEach { style ->
                val scheme =
                    rememberDynamicColorScheme(
                        primary =
                            if (isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                colorResource(android.R.color.system_accent1_200)
                            } else seedColor,
                        isDark =
                            when (appTheme) {
                                SYSTEM -> isSystemInDarkTheme()
                                DARK -> true
                                LIGHT -> false
                            },
                        isAmoled = isAmoled,
                        style = style.toMPaletteStyle(),
                    )
                val selected = paletteStyle == style

                Box(
                    modifier =
                        Modifier.size(50.dp)
                            .clip(
                                shape =
                                    if (selected) MaterialShapes.VerySunny.toShape()
                                    else CircleShape
                            )
                            .clickable(enabled = isUserSubscribed) { onClick(style) },
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val colors =
                            listOf(
                                scheme.primary,
                                scheme.primaryContainer,
                                scheme.secondary,
                                scheme.secondaryContainer,
                                scheme.tertiary,
                                scheme.tertiaryContainer,
                            )
                        val sweepAngle = 360f / colors.size
                        colors.forEachIndexed { index, color ->
                            drawArc(
                                color = color,
                                startAngle = index * sweepAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                            )
                        }
                    }

                    Box(
                        modifier =
                            Modifier.matchParentSize()
                                .background(
                                    color = scheme.primary.copy(alpha = if (selected) 0.7f else 0f)
                                )
                    )

                    if (selected) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.check_circle),
                            contentDescription = null,
                            tint = scheme.onTertiary,
                        )
                    }
                }
            }
        }
    }
}
