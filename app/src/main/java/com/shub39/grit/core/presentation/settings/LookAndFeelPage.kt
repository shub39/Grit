package com.shub39.grit.core.presentation.settings

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.from
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.grit.R
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.presentation.components.PageFill
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelPage(
    datastore: GritDatastore = koinInject()
) = PageFill {
    var colorPickerDialog by remember { mutableStateOf(false) }

    val isDark by datastore.getDarkThemePref().collectAsState(false)
    val isAmoled by datastore.getAmoledPref().collectAsState(false)
    val paletteStyle by datastore.getPaletteStyle().collectAsState(PaletteStyle.TonalSpot)
    val seedColor by datastore.getSeedColorFlow().collectAsState(Color.White.toArgb())
    val isMaterialYou by datastore.getMaterialYouFlow().collectAsState(false)

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxSize(),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.look_and_feel)
                )
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_system_theme)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.use_system_theme_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = isDark == null,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    datastore.setDarkThemePref(
                                        if (it) null else true
                                    )
                                }
                            }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_dark_theme)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = isDark == true,
                            enabled = isDark != null,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    datastore.setDarkThemePref(it)
                                }
                            }
                        )
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
                            checked = isAmoled,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    datastore.setAmoledPref(it)
                                }
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
                                checked = isMaterialYou,
                                onCheckedChange = {
                                    coroutineScope.launch {
                                        datastore.setMaterialYou(it)
                                    }
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
                                containerColor = Color(seedColor),
                                contentColor = contentColorFor(Color(seedColor))
                            ),
                            enabled = !isMaterialYou
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
                    },
                    supportingContent = {
                        val scrollState = rememberScrollState()

                        Row(
                            modifier = Modifier
                                .horizontalScroll(scrollState)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                val scheme = rememberDynamicColorScheme(
                                    primary = if (isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        colorResource(android.R.color.system_accent1_200)
                                    } else Color(seedColor),
                                    isDark = isDark == true,
                                    isAmoled = isAmoled,
                                    style = style
                                )

                                SelectableMiniPalette(
                                    selected = paletteStyle == style,
                                    onClick = {
                                        coroutineScope.launch {
                                            datastore.setPaletteStyle(style)
                                        }
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
                )
            }
        }
    }

    if (colorPickerDialog) {
        val controller = rememberColorPickerController()

        BasicAlertDialog(
            onDismissRequest = { colorPickerDialog = false }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .width(350.dp)
                            .height(300.dp)
                            .padding(top = 10.dp),
                        initialColor = Color(seedColor),
                        controller = controller
                    )

                    BrightnessSlider(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .height(35.dp),
                        initialColor = Color(seedColor),
                        controller = controller
                    )

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(vertical = 10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = controller
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                datastore.setSeedColor(controller.selectedColor.value.toArgb())
                            }

                            colorPickerDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.done),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.inverseOnSurface,
    ) {
        TooltipBox(
            modifier = modifier,
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
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