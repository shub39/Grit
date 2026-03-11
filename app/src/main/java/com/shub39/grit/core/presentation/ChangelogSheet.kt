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
package com.shub39.grit.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.domain.VersionEntry
import com.shub39.grit.core.shared_ui.GritBottomSheet
import com.shub39.grit.core.shared_ui.detachedItemShape
import com.shub39.grit.core.shared_ui.endItemShape
import com.shub39.grit.core.shared_ui.leadingItemShape
import com.shub39.grit.core.shared_ui.listItemColors
import com.shub39.grit.core.shared_ui.middleItemShape
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import com.shub39.grit.core.theme.flexFontEmphasis
import com.shub39.grit.core.theme.flexFontRounded
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.done
import grit.shared.core.generated.resources.settings
import grit.shared.core.generated.resources.whats_changed
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChangelogSheet(
    modifier: Modifier = Modifier,
    currentLog: VersionEntry,
    onDismissRequest: () -> Unit,
) {
    GritBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier, padding = 0.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialShapes.Pill.toShape(),
                        ),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.settings),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Column {
                Text(
                    text = stringResource(Res.string.whats_changed),
                    style =
                        MaterialTheme.typography.headlineSmall.copy(fontFamily = flexFontEmphasis()),
                )
                Text(
                    text = currentLog.version,
                    style =
                        MaterialTheme.typography.titleMedium.copy(fontFamily = flexFontRounded()),
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                itemsIndexed(currentLog.changes) { index, change ->
                    val shape =
                        when {
                            currentLog.changes.size == 1 -> detachedItemShape()
                            index == 0 -> leadingItemShape()
                            index == currentLog.changes.size - 1 -> endItemShape()
                            else -> middleItemShape()
                        }

                    ListItem(
                        colors = listItemColors(),
                        modifier = Modifier.clip(shape),
                        headlineContent = { Text(text = change) },
                    )
                }
            }

            Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(Res.string.done))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        ChangelogSheet(
            currentLog =
                VersionEntry(
                    version = "1.0.0",
                    changes =
                        listOf(
                            "Changed X to y",
                            "Added ability to read the epstein files",
                            "removed herobrine",
                            "Got rid of the nuclear fission particle accelerator",
                        ),
                ),
            onDismissRequest = {},
        )
    }
}
