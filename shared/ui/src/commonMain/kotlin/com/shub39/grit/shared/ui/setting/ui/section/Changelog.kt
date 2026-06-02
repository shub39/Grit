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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.app.Changelog
import com.shub39.grit.core.app.VersionEntry
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.components.middleItemShape
import com.shub39.grit.shared.ui.theme.GritTheme
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun Changelog(modifier: Modifier = Modifier, changelog: Changelog, onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(Res.string.changelog),
                        fontFamily = flexFontEmphasis(),
                    )
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
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding =
                PaddingValues(
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = padding.calculateBottomPadding() + 60.dp,
                    start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                    end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                ),
        ) {
            changelog.forEach { versionEntry ->
                item {
                    Text(
                        text = versionEntry.version,
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = flexFontRounded()
                            ),
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                itemsIndexed(versionEntry.changes) { index, change ->
                    val shape =
                        when {
                            versionEntry.changes.size == 1 -> detachedItemShape()
                            index == 0 -> leadingItemShape()
                            index == versionEntry.changes.size - 1 -> endItemShape()
                            else -> middleItemShape()
                        }

                    ListItem(
                        colors = listItemColors(),
                        modifier = Modifier.clip(shape),
                        headlineContent = { Text(text = change) },
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Preview
@Composable
private fun ChangelogPreview() {
    GritTheme {
        Changelog(
            changelog =
                listOf(
                    VersionEntry(
                        version = "1.0.0",
                        changes = listOf("Initial release", "Added new feature"),
                    ),
                    VersionEntry(
                        version = "1.1.0",
                        changes = listOf("Bug fixes", "Improved performance"),
                    ),
                ),
            onNavigateBack = {},
        )
    }
}
