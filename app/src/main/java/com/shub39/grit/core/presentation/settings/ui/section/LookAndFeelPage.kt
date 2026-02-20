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
package com.shub39.grit.core.presentation.settings.ui.section

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.core.presentation.component.ColorPickerDialog
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.component.endItemShape
import com.shub39.grit.core.presentation.settings.ui.component.leadingItemShape
import com.shub39.grit.core.presentation.settings.ui.component.listItemColors
import com.shub39.grit.core.presentation.settings.ui.component.middleItemShape
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.AppTheme.Companion.toDisplayString
import com.shub39.grit.core.theme.Fonts
import com.shub39.grit.core.theme.Fonts.Companion.toDisplayString
import com.shub39.grit.core.theme.Fonts.Companion.toFontRes
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.app_theme
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.check_circle
import grit.shared.core.generated.resources.dark_mode
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.font
import grit.shared.core.generated.resources.light_mode
import grit.shared.core.generated.resources.look_and_feel
import grit.shared.core.generated.resources.material_theme
import grit.shared.core.generated.resources.material_theme_desc
import grit.shared.core.generated.resources.palette
import grit.shared.core.generated.resources.palette_style
import grit.shared.core.generated.resources.select_seed
import grit.shared.core.generated.resources.select_seed_desc
import grit.shared.core.generated.resources.unlock_more_plus
import grit.shared.core.generated.resources.use_amoled
import grit.shared.core.generated.resources.use_amoled_desc
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeelPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    isUserSubscribed: Boolean,
    onNavigateToPaywall: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var colorPickerDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(Res.string.look_and_feel)) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_back),
                        contentDescription = "Navigate Back",
                    )
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 60.dp),
        ) {
            item {
                // appTheme picker
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Column(modifier = Modifier.clip(leadingItemShape())) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector =
                                        vectorResource(
                                            when (state.theme.appTheme) {
                                                AppTheme.SYSTEM -> {
                                                    if (isSystemInDarkTheme())
                                                        Res.drawable.dark_mode
                                                    else Res.drawable.light_mode
                                                }

                                                AppTheme.DARK -> Res.drawable.dark_mode
                                                AppTheme.LIGHT -> Res.drawable.light_mode
                                            }
                                        ),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = { Text(text = stringResource(Res.string.app_theme)) },
                            colors = listItemColors(),
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                        ) {
                            AppTheme.entries.forEach { appTheme ->
                                ToggleButton(
                                    checked = appTheme == state.theme.appTheme,
                                    onCheckedChange = {
                                        onAction(SettingsAction.ChangeAppTheme(appTheme))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors =
                                        ToggleButtonDefaults.toggleButtonColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.surfaceContainerLow
                                        ),
                                ) {
                                    Text(text = stringResource(appTheme.toDisplayString()))
                                }
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(Res.string.material_theme))
                            },
                            supportingContent = {
                                Text(text = stringResource(Res.string.material_theme_desc))
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.theme.isMaterialYou,
                                    onCheckedChange = {
                                        onAction(SettingsAction.ChangeMaterialYou(it))
                                    },
                                )
                            },
                            colors = listItemColors(),
                            modifier =
                                Modifier.clip(
                                    if (isUserSubscribed) middleItemShape() else endItemShape()
                                ),
                        )
                    }

                    // plus redirect
                    if (!isUserSubscribed) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillParentMaxWidth().height(60.dp),
                        ) {
                            LinearWavyProgressIndicator(
                                progress = { 0.90f },
                                modifier = Modifier.fillParentMaxWidth(),
                            )

                            Button(onClick = onNavigateToPaywall) {
                                Text(text = stringResource(Res.string.unlock_more_plus))
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier =
                            Modifier.clip(
                                if (isUserSubscribed) middleItemShape() else leadingItemShape()
                            )
                    ) {
                        ListItem(
                            headlineContent = { Text(text = stringResource(Res.string.font)) },
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.font),
                                    contentDescription = null,
                                )
                            },
                            colors = listItemColors(),
                        )

                        FlowRow(
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Fonts.entries.forEach { font ->
                                ToggleButton(
                                    checked = state.theme.font == font,
                                    onCheckedChange = {
                                        onAction(SettingsAction.ChangeFontPref(font))
                                    },
                                    colors =
                                        ToggleButtonDefaults.toggleButtonColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.surfaceContainerLow
                                        ),
                                ) {
                                    Text(
                                        text = font.toDisplayString(),
                                        fontFamily =
                                            font.toFontRes()?.let { FontFamily(Font(it)) }
                                                ?: FontFamily.Default,
                                    )
                                }
                            }
                        }
                    }

                    ListItem(
                        headlineContent = { Text(text = stringResource(Res.string.use_amoled)) },
                        supportingContent = {
                            Text(text = stringResource(Res.string.use_amoled_desc))
                        },
                        trailingContent = {
                            Switch(
                                checked = state.theme.isAmoled,
                                enabled = isUserSubscribed,
                                onCheckedChange = { onAction(SettingsAction.ChangeAmoled(it)) },
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape()),
                    )

                    AnimatedVisibility(visible = !state.theme.isMaterialYou) {
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(Res.string.select_seed))
                            },
                            supportingContent = {
                                Text(text = stringResource(Res.string.select_seed_desc))
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { colorPickerDialog = true },
                                    colors =
                                        IconButtonDefaults.iconButtonColors(
                                            containerColor = state.theme.seedColor,
                                            contentColor = contentColorFor(state.theme.seedColor),
                                        ),
                                    enabled = isUserSubscribed,
                                ) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.edit),
                                        contentDescription = "Select Color",
                                    )
                                }
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape()),
                        )
                    }

                    // palette style picker
                    Column(modifier = Modifier.clip(endItemShape())) {
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(Res.string.palette_style))
                            },
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.palette),
                                    contentDescription = null,
                                )
                            },
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                val scheme =
                                    rememberDynamicColorScheme(
                                        primary =
                                            if (
                                                state.theme.isMaterialYou &&
                                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                                            ) {
                                                colorResource(android.R.color.system_accent1_200)
                                            } else state.theme.seedColor,
                                        isDark =
                                            when (state.theme.appTheme) {
                                                AppTheme.SYSTEM -> isSystemInDarkTheme()
                                                AppTheme.DARK -> true
                                                AppTheme.LIGHT -> false
                                            },
                                        isAmoled = state.theme.isAmoled,
                                        style = style,
                                    )
                                val selected = state.theme.paletteStyle == style

                                Box(
                                    modifier =
                                        Modifier.size(50.dp)
                                            .background(
                                                color = scheme.tertiary,
                                                shape =
                                                    if (selected) MaterialShapes.VerySunny.toShape()
                                                    else CircleShape,
                                            )
                                            .clickable {
                                                onAction(SettingsAction.ChangePaletteStyle(style))
                                            },
                                    contentAlignment = Alignment.Center,
                                ) {
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
            }
        }
    }

    if (colorPickerDialog) {
        ColorPickerDialog(
            initialColor = state.theme.seedColor,
            onSelect = { onAction(SettingsAction.ChangeSeedColor(it)) },
            onDismiss = { colorPickerDialog = false },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        Surface {
            LookAndFeelPage(
                state = SettingsState(),
                isUserSubscribed = true,
                onAction = {},
                onNavigateBack = {},
                onNavigateToPaywall = {},
            )
        }
    }
}
