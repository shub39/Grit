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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.setting.ui.component.LicenseBottomSheet
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun About(versionName: String, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uriHandler = LocalUriHandler.current

    var showLicenseBottomSheet by remember { mutableStateOf(false) }

    if (showLicenseBottomSheet) {
        LicenseBottomSheet(onDismissRequest = { showLicenseBottomSheet = false })
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(Res.string.about), fontFamily = flexFontEmphasis())
                },
                navigationIcon = {
                    FilledTonalIconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.nav_arrow_back),
                            contentDescription = "Navigate Back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding =
                PaddingValues(
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = padding.calculateBottomPadding() + 60.dp,
                    start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                    end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                ),
        ) {
            aboutApp(versionName = versionName, uriHandler = uriHandler)
            engagementLinks(uriHandler)
            item {
                ListItem(
                    colors = listItemColors(),
                    leadingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.license),
                            contentDescription = null,
                        )
                    },
                    headlineContent = { Text(text = "License") },
                    supportingContent = { Text(text = "GPL-3.0 License") },
                    modifier =
                        Modifier.clip(detachedItemShape()).clickable {
                            showLicenseBottomSheet = true
                        },
                )
            }
        }
    }
}

private fun LazyListScope.engagementLinks(uriHandler: UriHandler) {
    item {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ListItem(
                colors = listItemColors(),
                leadingContent = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.buymeacoffee),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.open_link),
                        contentDescription = null,
                    )
                },
                headlineContent = { Text(text = stringResource(Res.string.bmc)) },
                supportingContent = { Text(text = stringResource(Res.string.bmc_desc)) },
                modifier =
                    Modifier.clip(leadingItemShape()).clickable {
                        uriHandler.openUri("https://buymeacoffee.com/shub39")
                    },
            )
            ListItem(
                colors = listItemColors(),
                leadingContent = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.translate),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.open_link),
                        contentDescription = null,
                    )
                },
                headlineContent = { Text(text = stringResource(Res.string.translate)) },
                supportingContent = { Text(text = stringResource(Res.string.translate_desc)) },
                modifier =
                    Modifier.clip(endItemShape()).clickable {
                        uriHandler.openUri("https://hosted.weblate.org/engage/grit/")
                    },
            )
        }
    }
}

private fun LazyListScope.aboutApp(versionName: String, uriHandler: UriHandler) {
    item {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // App card
            Card(shape = leadingItemShape()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier.size(64.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialShapes.Cookie12Sided.toShape(),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.grit_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Grit",
                            style =
                                MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = flexFontRounded()
                                ),
                        )
                        Text(
                            text = versionName,
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                ),
                        )
                    }

                    Row {
                        FilledTonalIconButton(
                            onClick = { uriHandler.openUri("https://discord.gg/nxA2hgtEKf") }
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.discord),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        FilledTonalIconButton(
                            onClick = { uriHandler.openUri("https://github.com/shub39/Grit") }
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.github),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }

            // dev card
            Card(shape = endItemShape()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier.size(64.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = MaterialShapes.Square.toShape(),
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.dev_icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(48.dp),
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Shubham Gorai",
                                style =
                                    MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = flexFontRounded()
                                    ),
                            )
                            Text(
                                text = "Developer",
                                style =
                                    MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.tertiary
                                    ),
                            )
                        }
                    }

                    FlowRow(
                        modifier = Modifier.padding(start = 80.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(
                                "https://github.com/shub39" to Res.drawable.github,
                                "https://shub39.github.io/" to Res.drawable.language,
                                "mailto:cptnshubham39+grit_app@gmail.com" to Res.drawable.email,
                            )
                            .forEach { pair ->
                                FilledTonalIconButton(
                                    onClick = { uriHandler.openUri(pair.first) },
                                    modifier =
                                        Modifier.size(
                                            IconButtonDefaults.smallContainerSize(
                                                widthOption =
                                                    IconButtonDefaults.IconButtonWidthOption.Wide
                                            )
                                        ),
                                ) {
                                    Icon(
                                        imageVector = vectorResource(pair.second),
                                        contentDescription = null,
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}
