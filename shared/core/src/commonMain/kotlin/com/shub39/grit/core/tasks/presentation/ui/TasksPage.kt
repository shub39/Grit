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
package com.shub39.grit.core.tasks.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.shared_ui.GritDialog
import com.shub39.grit.core.tasks.presentation.TaskAction
import com.shub39.grit.core.tasks.presentation.TaskState
import com.shub39.grit.core.tasks.presentation.ui.component.CategoryUpsertSheet
import com.shub39.grit.core.tasks.presentation.ui.section.TaskList
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.cancel
import grit.shared.core.generated.resources.delete
import grit.shared.core.generated.resources.delete_category
import grit.shared.core.generated.resources.drag_indicator
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.edit_categories
import grit.shared.core.generated.resources.tasks
import grit.shared.core.generated.resources.warning
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun TasksPage(state: TaskState, onAction: (TaskAction) -> Unit) {
    var showCategoryEditor by remember { mutableStateOf(false) }

    TaskList(state = state, onAction = onAction, onEditCategories = { showCategoryEditor = true })

    if (showCategoryEditor) {
        CategoryEditDialog(
            state = state,
            onAction = onAction,
            onDismissRequest = { showCategoryEditor = false },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoryEditDialog(
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    onDismissRequest: () -> Unit,
) {
    GritDialog(onDismissRequest = onDismissRequest, padding = 0.dp) {
        var categories by remember(state.tasks) { mutableStateOf(state.tasks.keys.toList()) }

        val listState = rememberLazyListState()
        val reorderableListState =
            rememberReorderableLazyListState(listState) { from, to ->
                categories =
                    categories.toMutableList().apply { add(to.index, removeAt(from.index)) }

                onAction(
                    TaskAction.ReorderCategories(
                        categories.mapIndexed { index, category -> index to category }
                    )
                )
            }

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.edit),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )

            Text(
                text = stringResource(Res.string.edit_categories),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                itemsIndexed(categories, key = { _, it -> it.id }) { _, category ->
                    var showEditSheet by remember { mutableStateOf(false) }
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    ReorderableItem(reorderableListState, key = category.id) {
                        ListItem(
                            modifier = Modifier.clip(MaterialTheme.shapes.medium),
                            colors =
                                ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                            headlineContent = { Text(text = category.name, maxLines = 1) },
                            supportingContent = {
                                Text(
                                    text =
                                        "${state.tasks[category]?.size ?: "0"} ${
                                        stringResource(
                                            Res.string.tasks
                                        )
                                    }"
                                )
                            },
                            leadingContent = {
                                IconButton(onClick = { showEditSheet = true }) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.edit),
                                        contentDescription = "Edit",
                                    )
                                }
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { showDeleteDialog = true },
                                        enabled = categories.size > 1,
                                    ) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.delete),
                                            contentDescription = "Delete",
                                        )
                                    }

                                    AnimatedVisibility(visible = categories.size > 1) {
                                        Icon(
                                            imageVector =
                                                vectorResource(Res.drawable.drag_indicator),
                                            contentDescription = null,
                                            modifier =
                                                Modifier.padding(horizontal = 8.dp)
                                                    .draggableHandle(),
                                        )
                                    }
                                }
                            },
                        )
                    }

                    if (showDeleteDialog) {
                        GritDialog(onDismissRequest = { showDeleteDialog = false }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.warning),
                                contentDescription = null,
                            )

                            Text(
                                text = stringResource(Res.string.delete),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Text(
                                text = stringResource(Res.string.delete_category),
                                textAlign = TextAlign.Center,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    onClick = { showDeleteDialog = false },
                                    shapes =
                                        ButtonShapes(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            pressedShape = MaterialTheme.shapes.small,
                                        ),
                                ) {
                                    Text(stringResource(Res.string.cancel))
                                }

                                TextButton(
                                    onClick = {
                                        onAction(TaskAction.DeleteCategory(category))
                                        showDeleteDialog = false
                                    },
                                    shapes =
                                        ButtonShapes(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            pressedShape = MaterialTheme.shapes.small,
                                        ),
                                ) {
                                    Text(stringResource(Res.string.delete))
                                }
                            }
                        }
                    }

                    if (showEditSheet) {
                        CategoryUpsertSheet(
                            isEditSheet = true,
                            modifier = Modifier,
                            category = category,
                            onDismiss = { showEditSheet = false },
                            onUpsertCategory = {
                                onAction(TaskAction.AddCategory(it))
                                showEditSheet = false
                            },
                        )
                    }
                }
            }
        }
    }
}
