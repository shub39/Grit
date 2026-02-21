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
package com.shub39.grit.core.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.shub39.grit.core.shared_ui.GritDialog
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.done
import org.jetbrains.compose.resources.stringResource

// Color picker used app wide
@Composable
fun ColorPickerDialog(initialColor: Color, onSelect: (Color) -> Unit, onDismiss: () -> Unit) {
    val controller = rememberColorPickerController()

    GritDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.wrapContentSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HsvColorPicker(
                modifier = Modifier.width(350.dp).height(300.dp).padding(top = 10.dp),
                initialColor = initialColor,
                controller = controller,
            )

            BrightnessSlider(
                modifier = Modifier.padding(top = 10.dp).height(35.dp),
                initialColor = initialColor,
                controller = controller,
            )

            AlphaTile(
                modifier =
                    Modifier.size(80.dp).padding(vertical = 10.dp).clip(RoundedCornerShape(6.dp)),
                controller = controller,
            )

            Button(
                onClick = {
                    onSelect(controller.selectedColor.value)
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(Res.string.done),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
