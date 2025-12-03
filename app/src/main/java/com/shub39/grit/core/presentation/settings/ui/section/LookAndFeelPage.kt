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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.domain.Fonts
import com.shub39.grit.core.presentation.component.ColorPickerDialog
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.component.endItemShape
import com.shub39.grit.core.presentation.settings.ui.component.leadingItemShape
import com.shub39.grit.core.presentation.settings.ui.component.listItemColors
import com.shub39.grit.core.presentation.settings.ui.component.middleItemShape
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.core.presentation.theme.Theme
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.app_theme
import grit.shared.core.generated.resources.font
import grit.shared.core.generated.resources.look_and_feel
import grit.shared.core.generated.resources.material_theme
import grit.shared.core.generated.resources.material_theme_desc
import grit.shared.core.generated.resources.palette_style
import grit.shared.core.generated.resources.select_seed
import grit.shared.core.generated.resources.select_seed_desc
import grit.shared.core.generated.resources.unlock_more_plus
import grit.shared.core.generated.resources.use_amoled
import grit.shared.core.generated.resources.use_amoled_desc
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeelPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(Unit) {
        onAction(SettingsAction.OnCheckSubscription)
    }

    var colorPickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.look_and_feel)
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 60.dp),
        ) {
            item {
                // appTheme picker
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Column(
                        modifier = Modifier.clip(leadingItemShape())
                    ) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = when (state.theme.appTheme) {
                                        AppTheme.SYSTEM -> {
                                            if (isSystemInDarkTheme()) Icons.Rounded.DarkMode else Icons.Rounded.LightMode
                                        }
                                        AppTheme.DARK -> Icons.Rounded.DarkMode
                                        AppTheme.LIGHT -> Icons.Rounded.LightMode
                                    },
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.app_theme)
                                )
                            },
                            colors = listItemColors()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            AppTheme.entries.forEach { appTheme ->
                                ToggleButton(
                                    checked = appTheme == state.theme.appTheme,
                                    onCheckedChange = { onAction(SettingsAction.ChangeAppTheme(appTheme)) },
                                    modifier = Modifier.weight(1f),
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(text = stringResource(appTheme.fullName))
                                }
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.material_theme)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.material_theme_desc)
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
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(
                                if (state.isUserSubscribed) middleItemShape() else endItemShape()
                            )
                        )
                    }

                    // plus redirect
                    if (!state.isUserSubscribed) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .height(60.dp)
                        ) {
                            LinearWavyProgressIndicator(
                                progress = { 0.90f },
                                modifier = Modifier.fillParentMaxWidth()
                            )

                            Button(
                                onClick = { onAction(SettingsAction.OnPaywallShow) }
                            ) {
                                Text(
                                    text = stringResource(Res.string.unlock_more_plus)
                                )
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier = Modifier.clip(
                            if (state.isUserSubscribed) middleItemShape() else leadingItemShape()
                        )
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.font)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.FontDownload,
                                    contentDescription = null
                                )
                            },
                            colors = listItemColors()
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Fonts.entries.forEach { font ->
                                ToggleButton(
                                    checked = state.theme.font == font,
                                    onCheckedChange = {
                                        onAction(SettingsAction.ChangeFontPref(font))
                                    },
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(
                                        text = font.name.lowercase()
                                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                        fontFamily = FontFamily(Font(font.resource))
                                    )
                                }
                            }
                        }
                    }

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.use_amoled)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.use_amoled_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.theme.isAmoled,
                                enabled = state.isUserSubscribed,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangeAmoled(it))
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape())
                    )

                    AnimatedVisibility(
                        visible = !state.theme.isMaterialYou
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.select_seed)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.select_seed_desc)
                                )
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { colorPickerDialog = true },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = state.theme.seedColor,
                                        contentColor = contentColorFor(state.theme.seedColor)
                                    ),
                                    enabled = state.isUserSubscribed
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Create,
                                        contentDescription = "Select Color"
                                    )
                                }
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape())
                        )
                    }

                    // palette style picker
                    Column(
                        modifier = Modifier.clip(endItemShape())
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.palette_style)
                                )
                            },
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Palette,
                                    contentDescription = null
                                )
                            }
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
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
                                val selected = state.theme.paletteStyle == style

                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(
                                            color = scheme.tertiary,
                                            shape = if (selected) MaterialShapes.VerySunny.toShape() else CircleShape
                                        )
                                        .clickable {
                                            onAction(SettingsAction.ChangePaletteStyle(style))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = scheme.onTertiary
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
            onDismiss = { colorPickerDialog = false }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        Surface {
            LookAndFeelPage(
                state = SettingsState(isUserSubscribed = true),
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}