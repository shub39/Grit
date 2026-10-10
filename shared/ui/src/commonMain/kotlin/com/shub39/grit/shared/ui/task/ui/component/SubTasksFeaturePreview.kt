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
package com.shub39.grit.shared.ui.task.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shub39.grit.shared.ui.components.GritBottomSheet
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.add_subtasks
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubTasksFeaturePreview(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onOpenPaywall: () -> Unit,
) {
    GritBottomSheet(modifier = modifier, onDismissRequest = onDismissRequest) {
        Text(
            text = stringResource(Res.string.add_subtasks),
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = flexFontRounded()),
        )

        Button(
            onClick = onOpenPaywall,
            modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
        ) {
            Text(
                text = "Unlock with Plus",
                style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
            )
        }
    }
}
