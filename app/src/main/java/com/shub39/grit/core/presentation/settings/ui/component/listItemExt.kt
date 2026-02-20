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

private const val connectedCornerRadius = 4
private const val endCornerRadius = 16

@Composable
fun listItemColors(): ListItemColors {
    return ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
}

fun leadingItemShape(): Shape =
    RoundedCornerShape(
        topStart = endCornerRadius.dp,
        topEnd = endCornerRadius.dp,
        bottomEnd = connectedCornerRadius.dp,
        bottomStart = connectedCornerRadius.dp,
    )

fun middleItemShape(): Shape =
    RoundedCornerShape(
        topStart = connectedCornerRadius.dp,
        topEnd = connectedCornerRadius.dp,
        bottomStart = connectedCornerRadius.dp,
        bottomEnd = connectedCornerRadius.dp,
    )

fun endItemShape(): Shape =
    RoundedCornerShape(
        topStart = connectedCornerRadius.dp,
        topEnd = connectedCornerRadius.dp,
        bottomEnd = endCornerRadius.dp,
        bottomStart = endCornerRadius.dp,
    )

fun detachedItemShape(): Shape = RoundedCornerShape(endCornerRadius.dp)
