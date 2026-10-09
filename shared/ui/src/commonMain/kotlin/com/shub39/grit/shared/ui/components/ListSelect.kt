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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.arrow_back
import grit.shared.ui.generated.resources.arrow_forward
import grit.shared.ui.generated.resources.check
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
            val currentIndex = options.indexOf(selected)
            var expanded by remember { mutableStateOf(false) }

            val prevInteractionSource = remember { MutableInteractionSource() }
            val centerInteractionSource = remember { MutableInteractionSource() }
            val nextInteractionSource = remember { MutableInteractionSource() }

            val prevPressed by prevInteractionSource.collectIsPressedAsState()
            val prevHovered by prevInteractionSource.collectIsHoveredAsState()
            val prevWeight by animateFloatAsState(if (prevPressed || prevHovered) 0.25f else 0f)

            val centerPressed by centerInteractionSource.collectIsPressedAsState()
            val centerHovered by centerInteractionSource.collectIsHoveredAsState()
            val centerWeight by
                animateFloatAsState(if (centerPressed || centerHovered) 0.25f else 0f)

            val nextPressed by nextInteractionSource.collectIsPressedAsState()
            val nextHovered by nextInteractionSource.collectIsHoveredAsState()
            val nextWeight by animateFloatAsState(if (nextPressed || nextHovered) 0.25f else 0f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                ToggleButton(
                    checked = false,
                    enabled = enabled,
                    onCheckedChange = {
                        val prevIndex =
                            if (currentIndex > 0) currentIndex - 1 else options.lastIndex
                        onSelectedChange(options[prevIndex])
                    },
                    modifier = Modifier.weight(1f + prevWeight),
                    interactionSource = prevInteractionSource,
                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                    content = {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Previous",
                        )
                    },
                )

                Box(modifier = Modifier.weight(2f + centerWeight)) {
                    ToggleButton(
                        checked = true,
                        enabled = enabled,
                        onCheckedChange = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = centerInteractionSource,
                        shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                        content = { labelProvider(selected) },
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(10.dp, 10.dp),
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { labelProvider(option) },
                                onClick = {
                                    onSelectedChange(option)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (option == selected) {
                                        Icon(
                                            painter = painterResource(Res.drawable.check),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }

                ToggleButton(
                    checked = false,
                    enabled = enabled,
                    onCheckedChange = {
                        val nextIndex = (currentIndex + 1) % options.size
                        onSelectedChange(options[nextIndex])
                    },
                    modifier = Modifier.weight(1f + nextWeight),
                    interactionSource = nextInteractionSource,
                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                    content = {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_forward),
                            contentDescription = "Next",
                        )
                    },
                )
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
