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
package com.shub39.grit.core.presentation.settings.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private const val CONNECTED_CORNER_RADIUS = 4
private const val END_CORNER_RADIUS = 16

@Composable
fun listItemColors(): ListItemColors {
    return ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
}

fun leadingItemShape(): Shape =
    RoundedCornerShape(
        topStart = END_CORNER_RADIUS.dp,
        topEnd = END_CORNER_RADIUS.dp,
        bottomEnd = CONNECTED_CORNER_RADIUS.dp,
        bottomStart = CONNECTED_CORNER_RADIUS.dp,
    )

fun middleItemShape(): Shape =
    RoundedCornerShape(
        topStart = CONNECTED_CORNER_RADIUS.dp,
        topEnd = CONNECTED_CORNER_RADIUS.dp,
        bottomStart = CONNECTED_CORNER_RADIUS.dp,
        bottomEnd = CONNECTED_CORNER_RADIUS.dp,
    )

fun endItemShape(): Shape =
    RoundedCornerShape(
        topStart = CONNECTED_CORNER_RADIUS.dp,
        topEnd = CONNECTED_CORNER_RADIUS.dp,
        bottomEnd = END_CORNER_RADIUS.dp,
        bottomStart = END_CORNER_RADIUS.dp,
    )

fun detachedItemShape(): Shape = RoundedCornerShape(END_CORNER_RADIUS.dp)
