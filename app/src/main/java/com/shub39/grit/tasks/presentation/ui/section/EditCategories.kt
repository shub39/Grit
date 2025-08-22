package com.shub39.grit.tasks.presentation.ui.section

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
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
import com.shub39.grit.core.presentation.component.GritBottomSheet
import com.shub39.grit.core.presentation.component.GritDialog
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.tasks.presentation.TaskPageAction
import com.shub39.grit.tasks.presentation.TaskPageState
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditCategories(
    state: TaskPageState,
    onAction: (TaskPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    var categories by remember(state.tasks) { mutableStateOf(state.tasks.keys.toList()) }

    val listState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(listState) { from, to ->
        categories = categories.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        onAction(TaskPageAction.ReorderCategories(categories.mapIndexed { index, category -> index to category }))
    }

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
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
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
                var showEditSheet by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }

                ReorderableItem(reorderableListState, key = category.id) {
                    val cardCorners by animateDpAsState(
                        targetValue = if (it) 30.dp else 16.dp
                    )

                    ListItem(
                        headlineContent = {
                            Text(text = category.name)
                        },
                        supportingContent = {
                            Text(text = "${state.tasks[category]?.size ?: "0"} ${stringResource(R.string.tasks)}")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            leadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            supportingColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.clip(RoundedCornerShape(cardCorners)),
                        trailingContent = {
                            Row {
                                FilledTonalIconButton(
                                    onClick = { showDeleteDialog = true },
                                    enabled = categories.size > 1
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.round_delete_forever_24),
                                        contentDescription = "Delete"
                                    )
                                }

                                FilledTonalIconButton(
                                    onClick = { showEditSheet = true }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_edit_square_24),
                                        contentDescription = "Edit"
                                    )
                                }

                                IconButton(
                                    modifier = Modifier.draggableHandle(),
                                    onClick = {}
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_drag_indicator_24),
                                        contentDescription = null
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
                                onAction(TaskPageAction.DeleteCategory(category))
                                showDeleteDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shapes = ButtonShapes(
                                shape = MaterialTheme.shapes.extraLarge,
                                pressedShape = MaterialTheme.shapes.small
                            )
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    }
                }

                if (showEditSheet) {
                    GritBottomSheet(
                        onDismissRequest = { showEditSheet = false }
                    ) {
                        var name by remember { mutableStateOf(category.name) }
                        val keyboardController = LocalSoftwareKeyboardController.current
                        val focusRequester = remember { FocusRequester() }

                        LaunchedEffect(Unit) {
                            delay(200)
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Category"
                        )

                        Text(
                            text = stringResource(id = R.string.edit_categories),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            shape = MaterialTheme.shapes.medium,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )

                        Button(
                            onClick = {
                                onAction(
                                    TaskPageAction.AddCategory(
                                        category.copy(name = name)
                                    )
                                )
                                showEditSheet = false
                            },
                            shapes = ButtonShapes(
                                shape = MaterialTheme.shapes.extraLarge,
                                pressedShape = MaterialTheme.shapes.small
                            ),
                            modifier = Modifier.fillMaxWidth(),
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