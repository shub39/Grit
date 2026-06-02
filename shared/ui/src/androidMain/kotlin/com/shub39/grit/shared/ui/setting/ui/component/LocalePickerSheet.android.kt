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
package com.shub39.grit.shared.ui.setting.ui.component

import android.app.LocaleConfig
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shub39.grit.shared.ui.components.GritBottomSheet
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.components.segmentedListItemShapes
import grit.shared.ui.generated.resources.*
import java.util.Locale
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class AppLocale(val locale: Locale, val name: String)

// yeeted from nsh07/Tomato
@Composable
actual fun LocalePickerSheet(onDismissRequest: () -> Unit, modifier: Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentLocales = remember {
        if (Build.VERSION.SDK_INT >= 33) {
            context.getSystemService(LocaleManager::class.java).applicationLocales
        } else LocaleList.getEmptyLocaleList()
    }

    val supportedLocaleList: List<AppLocale>? = remember {
        if (Build.VERSION.SDK_INT >= 33) {
            val supportedLocales = LocaleConfig(context).supportedLocales
            if (supportedLocales != null) {
                buildList {
                        for (i in 0 until supportedLocales.size()) {
                            val locale = supportedLocales.get(i)
                            add(
                                AppLocale(
                                    locale,
                                    locale.getDisplayName(locale).replaceFirstChar {
                                        it.uppercase()
                                    },
                                )
                            )
                        }
                    }
                    .sortedBy { it.name }
            } else null
        } else null
    }

    val supportedLocalesSize = supportedLocaleList?.size ?: 0

    GritBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        padding = 16.dp,
    ) {
        if (supportedLocaleList != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(bottom = 60.dp),
                modifier = Modifier.heightIn(max = 600.dp).clip(shapes.large),
            ) {
                item {
                    SegmentedListItem(
                        onClick = {
                            scope
                                .launch {
                                    if (Build.VERSION.SDK_INT >= 33) {
                                        context
                                            .getSystemService(LocaleManager::class.java)
                                            .applicationLocales = LocaleList()
                                    }
                                    sheetState.hide()
                                }
                                .invokeOnCompletion { onDismissRequest() }
                        },
                        selected = currentLocales.isEmpty,
                        colors = listItemColors(),
                        shapes = segmentedListItemShapes(0, 1),
                        content = { Text(text = stringResource(Res.string.system_default)) },
                        trailingContent = {
                            if (currentLocales.isEmpty)
                                Icon(
                                    painter = painterResource(Res.drawable.check_circle),
                                    contentDescription = null,
                                )
                        },
                    )
                }

                item { Spacer(Modifier.height(12.dp)) }

                itemsIndexed(
                    items = supportedLocaleList,
                    key = { _: Int, it: AppLocale -> it.name },
                ) { index, it ->
                    val selected = !currentLocales.isEmpty && it.locale == currentLocales.get(0)

                    SegmentedListItem(
                        onClick = {
                            scope
                                .launch {
                                    if (Build.VERSION.SDK_INT >= 33) {
                                        context
                                            .getSystemService(LocaleManager::class.java)
                                            .applicationLocales = LocaleList(it.locale)
                                    }
                                    sheetState.hide()
                                }
                                .invokeOnCompletion { onDismissRequest() }
                        },
                        selected = selected,
                        content = { Text(text = it.name) },
                        shapes = segmentedListItemShapes(index, supportedLocalesSize),
                        colors = listItemColors(),
                    )
                }
            }
        }
    }
}
