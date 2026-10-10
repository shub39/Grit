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
package com.shub39.grit.shared.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.check
import grit.shared.ui.generated.resources.edit
import org.jetbrains.compose.resources.painterResource

/**
 * A composable that displays a title and a list of options as toggleable buttons, allowing the user
 * to select one option from the list. It's laid out in a FlowRow to accommodate a variable number
 * of options.
 *
 * @param T The type of the options in the list.
 * @param title The title text to be displayed above the selection options.
 * @param options A list of all available options of type [T] to be displayed.
 * @param selected The currently selected option of type [T].
 * @param onSelectedChange A callback that is invoked when a new option is selected.
 * @param labelProvider A composable lambda that defines how to display the label for each option.
 *   It receives an option of type [T] and is expected to render its UI representation.
 */
@Composable
fun <T> ListSelect(
    title: String?,
    options: List<T>,
    selected: T,
    onSelectedChange: (T) -> Unit,
    labelProvider: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        title?.let { Text(text = title, style = MaterialTheme.typography.titleMedium) }

        if (options.size > 3) {
            var expanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                TonalToggleButton(
                    checked = false,
                    enabled = enabled,
                    onCheckedChange = { expanded = true },
                    modifier = Modifier.height(ButtonDefaults.MinHeight).weight(0.7f),
                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                    content = { labelProvider(selected) },
                )

                Box(modifier = Modifier.weight(0.3f)) {
                    TonalToggleButton(
                        checked = expanded,
                        enabled = enabled,
                        onCheckedChange = { expanded = true },
                        modifier = Modifier.height(ButtonDefaults.MinHeight).fillMaxWidth(),
                        shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                        content = {
                            Icon(
                                painter = painterResource(Res.drawable.edit),
                                contentDescription = "Edit",
                            )
                        },
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(10.dp, 10.dp),
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        border = null,
                    ) {
                        options.forEachIndexed { index, option ->
                            if (option == selected) {
                                DropdownMenuItem(
                                    text = { labelProvider(option) },
                                    onCheckedChange = {
                                        onSelectedChange(option)
                                        expanded = false
                                    },
                                    shapes = MenuDefaults.itemShape(index, options.size),
                                    checked = option == selected,
                                    colors = MenuDefaults.selectableItemVibrantColors(),
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.check),
                                            contentDescription = null
                                        )
                                    },
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { labelProvider(option) },
                                    onCheckedChange = {
                                        onSelectedChange(option)
                                        expanded = false
                                    },
                                    colors = MenuDefaults.selectableItemVibrantColors(),
                                    shapes = MenuDefaults.itemShape(index, options.size),
                                    checked = option == selected,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
            ) {
                options.forEachIndexed { index, option ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val isHovered by interactionSource.collectIsHoveredAsState()
                    val animatedWeight by
                        animateFloatAsState(
                            targetValue =
                                if (option == selected || isHovered || isPressed) 0.25f else 0f
                        )

                    ToggleButton(
                        checked = option == selected,
                        enabled = enabled,
                        onCheckedChange = { onSelectedChange(option) },
                        content = { labelProvider(option) },
                        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                        modifier = Modifier.weight(1f + animatedWeight),
                        interactionSource = interactionSource,
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex ->
                                    ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                    )
                }
            }
        }
    }
}
