package com.shub39.grit.core.presentation.settings.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.from
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.R
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.domain.Fonts
import com.shub39.grit.core.presentation.components.ColorPickerDialog
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CloudSun
import compose.icons.fontawesomeicons.solid.Font
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeelPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {

    var colorPickerDialog by remember { mutableStateOf(false) }
    var appThemeDialog by remember { mutableStateOf(false) }
    var fontPickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize(),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.look_and_feel)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.app_theme)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.app_theme_desc)
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { appThemeDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Select Theme"
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.font)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.font_desc)
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { fontPickerDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Select font"
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_amoled)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.use_amoled_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.theme.isAmoled,
                            onCheckedChange = {
                                onAction(
                                    SettingsAction.ChangeAmoled(it)
                                )
                            }
                        )
                    }
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.material_theme)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.material_theme_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.theme.isMaterialYou,
                                onCheckedChange = {
                                    onAction(
                                        SettingsAction.ChangeMaterialYou(it)
                                    )
                                }
                            )
                        }
                    )
                }
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.select_seed)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.select_seed_desc)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { colorPickerDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = state.theme.seedColor,
                                contentColor = contentColorFor(state.theme.seedColor)
                            ),
                            enabled = !state.theme.isMaterialYou
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Select Color"
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.palette_style)
                        )
                    }
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(PaletteStyle.entries.toList(), key = { it.name }) { style ->
                        val scheme = rememberDynamicColorScheme(
                            primary = if (state.theme.isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                colorResource(android.R.color.system_accent1_200)
                            } else state.theme.seedColor,
                            isDark = when (state.theme.appTheme) {
                                AppTheme.SYSTEM -> isSystemInDarkTheme()
                                AppTheme.DARK -> true
                                AppTheme.LIGHT -> false
                            },
                            isAmoled = state.theme.isAmoled,
                            style = style
                        )

                        SelectableMiniPalette(
                            selected = state.theme.paletteStyle == style,
                            onClick = {
                                onAction(
                                    SettingsAction.ChangePaletteStyle(style)
                                )
                            },
                            contentDescription = { style.name },
                            accents = listOf(
                                TonalPalette.from(scheme.primary),
                                TonalPalette.from(scheme.tertiary),
                                TonalPalette.from(scheme.secondary)
                            )
                        )
                    }
                }
            }
        }
    }

    if (fontPickerDialog) {
        GritDialog(
            onDismissRequest = { fontPickerDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Font,
                    contentDescription = "Select App Font",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = stringResource(R.string.font),
                    style = MaterialTheme.typography.titleLarge
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Fonts.entries.forEach { font ->
                        ToggleButton(
                            checked = state.theme.font == font,
                            onCheckedChange = {
                                onAction(SettingsAction.ChangeFontPref(font))
                                fontPickerDialog = false
                            }
                        ) {
                            Text(
                                text = font.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                fontFamily = FontFamily(Font(font.resource))
                            )
                        }
                    }
                }
            }
        }
    }

    if (appThemeDialog) {
        GritDialog(
            onDismissRequest = { appThemeDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.CloudSun,
                    contentDescription = "Select App Theme",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = stringResource(R.string.app_theme),
                    style = MaterialTheme.typography.titleLarge
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AppTheme.entries.forEach { appTheme ->
                        ToggleButton(
                            checked = state.theme.appTheme == appTheme,
                            onCheckedChange = {
                                onAction(SettingsAction.ChangeAppTheme(appTheme))
                                appThemeDialog = false
                            }
                        ) {
                            Text(text = stringResource(appTheme.fullName))
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
            onDismiss = { colorPickerDialog = false }
        )
    }
}

// yeeted from https://github.com/SkyD666/PodAura/blob/master/app/src/main/java/com/skyd/anivu/ui/screen/settings/appearance/AppearanceScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectableMiniPalette(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: () -> String,
    accents: List<TonalPalette>,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        TooltipBox(
            modifier = modifier,
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(contentDescription())
                }
            },
            state = rememberTooltipState()
        ) {
            Surface(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(12.dp)
                    .size(50.dp),
                shape = CircleShape,
                color = Color(accents[0].tone(60))
            ) {
                Box {
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .offset((-25).dp, 25.dp),
                        color = Color(accents[1].tone(85)),
                    ) {}
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .offset(25.dp, 25.dp),
                        color = Color(accents[2].tone(75)),
                    ) {}
                    val animationSpec = spring<Float>(stiffness = Spring.StiffnessMedium)
                    AnimatedVisibility(
                        visible = selected,
                        enter = scaleIn(animationSpec) + fadeIn(animationSpec),
                        exit = scaleOut(animationSpec) + fadeOut(animationSpec),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Checked",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(16.dp),
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        }
    }
}