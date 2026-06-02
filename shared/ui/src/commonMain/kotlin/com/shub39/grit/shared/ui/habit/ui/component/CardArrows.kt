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
package com.shub39.grit.shared.ui.habit.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardArrows(
    onBackAction: () -> Unit,
    onForwardAction: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onExpandAction: (() -> Unit)? = null,
) {
    Row(modifier = modifier) {
        IconButton(onClick = onBackAction, enabled = enabled) {
            Icon(
                painter = painterResource(Res.drawable.arrow_back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        IconButton(onClick = onForwardAction, enabled = enabled) {
            Icon(
                painter = painterResource(Res.drawable.arrow_forward),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        onExpandAction?.let {
            FilledTonalIconButton(
                onClick = onExpandAction,
                shapes = IconButtonDefaults.shapes(),
                enabled = enabled,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.expand),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
