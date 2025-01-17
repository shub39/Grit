package com.shub39.grit.tasks.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.BetterIconButton
import com.shub39.grit.core.presentation.GritDialog
import com.shub39.grit.tasks.presentation.task_page.TaskPageAction
import com.shub39.grit.tasks.presentation.task_page.TaskPageState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategories(
    state: TaskPageState,
    action: (TaskPageAction) -> Unit
) {
    var categories by remember(state.tasks) { mutableStateOf(state.tasks.keys.toList()) }

    val listState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(listState) { from, to ->
        categories = categories.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        action(TaskPageAction.ReorderCategories(categories.mapIndexed { index, category -> index to category }))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.categories)
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(categories, key = { _, it -> it.id }) { index, category ->
                    var showEditDialog by remember { mutableStateOf(false) }
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    ReorderableItem(reorderableListState, key = category.id) {
                        ListItem(
                            headlineContent = {
                                Text(text = category.name)
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.clip(MaterialTheme.shapes.large),
                            leadingContent = {
                                IconButton(
                                    modifier = Modifier.draggableHandle(),
                                    onClick = {}
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_drag_indicator_24),
                                        contentDescription = null
                                    )
                                }
                            },
                            trailingContent = {
                                Row {
                                    BetterIconButton(
                                        onClick = { showDeleteDialog = true },
                                        enabled = categories.size > 1
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.round_delete_forever_24),
                                            contentDescription = "Delete"
                                        )
                                    }

                                    BetterIconButton(
                                        onClick = { showEditDialog = true }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_edit_square_24),
                                            contentDescription = "Edit"
                                        )
                                    }
                                }
                            }
                        )
                    }

                    if (showDeleteDialog) {
                        GritDialog(
                            onDismissRequest = { showDeleteDialog = false }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_warning_24),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )

                            Text(
                                text = stringResource(id = R.string.delete_category),
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = {
                                    action(TaskPageAction.DeleteCategory(category))
                                    showDeleteDialog = false
                                }
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                        }
                    }

                    if (showEditDialog) {
                        GritDialog(
                            onDismissRequest = { showEditDialog = false }
                        ) {
                            var name by remember { mutableStateOf("") }
                            val keyboardController = LocalSoftwareKeyboardController.current
                            val focusRequester = remember { FocusRequester() }

                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                shape = MaterialTheme.shapes.medium,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                modifier = Modifier.focusRequester(focusRequester),
                                label = { Text(text = stringResource(id = R.string.edit_categories)) }
                            )

                            Button(
                                onClick = {
                                    action(
                                        TaskPageAction.AddCategory(
                                            category.copy(
                                                name = name
                                            )
                                        )
                                    )
                                    showEditDialog = false
                                },
                                enabled = name.isNotBlank() && name.length <= 20
                            ) {
                                Text(text = stringResource(R.string.done))
                            }
                        }
                    }
                }
            }
        }
    }
}