package com.shub39.grit.tasks.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.Empty
import com.shub39.grit.core.presentation.components.GritBottomSheet
import com.shub39.grit.core.presentation.components.GritDialog
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.CategoryColors
import com.shub39.grit.tasks.domain.Task
import com.shub39.grit.tasks.domain.TaskPriority
import com.shub39.grit.tasks.presentation.TaskPageAction
import com.shub39.grit.tasks.presentation.TaskPageState
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    state: TaskPageState,
    onAction: (TaskPageAction) -> Unit,
    onNavigateToEditCategories: () -> Unit
) {
    var showTaskAddSheet by remember { mutableStateOf(false) }
    var showCategoryAddSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editState by remember { mutableStateOf(false) }
    var editTask: Task? by remember { mutableStateOf(null) }

    val tasks = state.tasks[state.currentCategory] ?: emptyList()

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
                title = { Text(text = stringResource(R.string.tasks)) },
                subtitle = {
                    Text(
                        text = "${state.completedTasks.size} " + stringResource(R.string.items_completed)
                    )
                },
                actions = {
                    Row {
                        AnimatedVisibility(
                            visible = state.completedTasks.isNotEmpty()
                        ) {
                            OutlinedIconButton(
                                onClick = { showDeleteDialog = true },
                                shapes = IconButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = MaterialTheme.shapes.small
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = tasks.isNotEmpty()
                        ) {
                            FilledTonalIconToggleButton(
                                checked = editState,
                                shapes = IconToggleButtonShapes(
                                    shape = CircleShape,
                                    checkedShape = MaterialTheme.shapes.small,
                                    pressedShape = MaterialTheme.shapes.extraSmall,
                                ),
                                onCheckedChange = { editState = it },
                                enabled = !state.tasks[state.currentCategory].isNullOrEmpty()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_reorder_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                items(state.tasks.keys.toList(), key = { it.id }) { category ->
                    val chipCorner by animateDpAsState(
                        targetValue = if (category == state.currentCategory) 20.dp else 5.dp
                    )

                    FilterChip(
                        selected = category == state.currentCategory,
                        onClick = {
                            onAction(TaskPageAction.ReorderTasks(tasks.mapIndexed { index, task -> index to task }))
                            onAction(TaskPageAction.ChangeCategory(category))
                            editState = false
                        },
                        shape = RoundedCornerShape(chipCorner),
                        label = { Text(category.name) }
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { showCategoryAddSheet = true },
                            enabled = !editState,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = AssistChipDefaults.assistChipBorder(false),
                            label = {
                                Icon(
                                    painter = painterResource(R.drawable.round_add_24),
                                    contentDescription = "Add Category"
                                )
                            }
                        )

                        AssistChip(
                            onClick = onNavigateToEditCategories,
                            enabled = !editState,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = AssistChipDefaults.assistChipBorder(false),
                            label = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_edit_square_24),
                                    contentDescription = "Edit Categories"
                                )
                            }
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = state.currentCategory
            ) { category ->
                if (category != null) {
                    var reorderableTasks by remember(state) { mutableStateOf(tasks) }
                    val lazyListState = rememberLazyListState()
                    val reorderableListState =
                        rememberReorderableLazyListState(lazyListState) { from, to ->
                            reorderableTasks = tasks.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }
                        }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(reorderableTasks, key = { _, it -> it.id }) { index, task ->
                            ReorderableItem(reorderableListState, key = task.id) {
                                val cardCorners by animateDpAsState(
                                    targetValue = if (it) 100.dp else 16.dp
                                )

                                TaskCard(
                                    task = task,
                                    dragState = editState,
                                    reorderIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_drag_indicator_24),
                                            contentDescription = "Drag",
                                            modifier = Modifier.draggableHandle(
                                                onDragStopped = {
                                                    onAction(TaskPageAction.ReorderTasks(reorderableTasks.mapIndexed { index, task -> index to task }))
                                                }
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(cardCorners),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                                        .combinedClickable(
                                            // change status on click
                                            onClick = {
                                                if (!editState) {
                                                    task.status = !task.status

                                                    onAction(TaskPageAction.UpsertTask(task))

                                                    if (task.status) {
                                                        reorderableTasks = tasks.toMutableList().apply {
                                                            add(tasks.size - 1, removeAt(index))
                                                        }

                                                        onAction(TaskPageAction.ReorderTasks(reorderableTasks.mapIndexed { index, task -> index to task }))
                                                    }
                                                }
                                            },
                                            // edit on click and hold
                                            onLongClick = {
                                                if (!editState) {
                                                    editTask = task
                                                }
                                            }
                                        )
                                )
                            }
                        }

                        item {
                            if (tasks.isEmpty()) {
                                Empty()
                            } else {
                                Spacer(modifier = Modifier.padding(60.dp))
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.currentCategory != null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { showTaskAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_add_24),
                        contentDescription = null
                    )

                    AnimatedVisibility(
                        visible = state.tasks[state.currentCategory].isNullOrEmpty()
                    ) {
                        Text(
                            text = stringResource(R.string.add_task),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // delete dialog
    if (showDeleteDialog) {
        GritDialog(
            onDismissRequest = { showDeleteDialog = false }
        ) {
            Icon(
                painter = painterResource(R.drawable.round_warning_24),
                contentDescription = "Warning",
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = stringResource(id = R.string.delete_tasks),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    onAction(TaskPageAction.DeleteTasks)
                    showDeleteDialog = false
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }

    if (showCategoryAddSheet) {
        var name by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritBottomSheet(
            onDismissRequest = {
                focusRequester.freeFocus()
                keyboardController?.hide()
                showCategoryAddSheet = false
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )

            Text(
                text = stringResource(R.string.add_category),
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
                            Category(
                                name = name,
                                color = CategoryColors.GRAY.color
                            )
                        )
                    )
                    showCategoryAddSheet = false
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

    //edit sheet
    if (editTask != null) {
        var newTitle by remember { mutableStateOf(editTask!!.title) }
        var newTaskCategoryId by remember { mutableLongStateOf(editTask!!.categoryId) }
        var editPriority by remember { mutableStateOf(editTask!!.priority) }
        var editDeadline by remember { mutableStateOf(editTask!!.deadline) }
        var showEditDatePicker by remember { mutableStateOf(false) }
        
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritBottomSheet(
            onDismissRequest = {
                focusRequester.freeFocus()
                keyboardController?.hide()
                editTask = null
            }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )

            Text(
                text = stringResource(id = R.string.edit_task),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            FlowRow(
                horizontalArrangement = Arrangement.Center
            ) {
                state.tasks.keys.forEach { category ->
                    ToggleButton(
                        checked = category.id == newTaskCategoryId,
                        onCheckedChange = { newTaskCategoryId = category.id },
                        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                        modifier = Modifier.padding(horizontal = 4.dp),
                        content = { Text(category.name) }
                    )
                }
            }

            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.None
                ),
                keyboardActions = KeyboardActions(
                    onAny = {
                        newTitle.plus("\n")
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text(text = stringResource(id = R.string.edit_task)) }
            )
            
            // Priority selection
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                TaskPriority.values().forEachIndexed { index, priority ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = TaskPriority.values().size
                        ),
                        onClick = { editPriority = priority },
                        selected = editPriority == priority,
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = when (priority) {
                                TaskPriority.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                TaskPriority.LOW -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    ) {
                        Text(priority.name)
                    }
                }
            }
            
            // Deadline selection
            Text(
                text = "Deadline (Optional)",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            
            OutlinedButton(
                onClick = { showEditDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = editDeadline?.format(
                        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                    ) ?: "Set deadline"
                )
            }
            
            if (editDeadline != null) {
                TextButton(
                    onClick = { editDeadline = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear deadline")
                }
            }

            Button(
                onClick = {
                    onAction(
                        TaskPageAction.UpsertTask(
                            editTask!!.copy(
                                title = newTitle,
                                categoryId = newTaskCategoryId,
                                priority = editPriority,
                                deadline = editDeadline
                            )
                        )
                    )
                    editTask = null
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = newTitle.isNotBlank() && newTitle.length <= 100
            ) {
                Text(stringResource(R.string.edit_task))
            }
        }
        
        // Edit task date picker dialog
        if (showEditDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = editDeadline?.atZone(java.time.ZoneOffset.systemDefault())?.toInstant()?.toEpochMilli()
            )
            val timePickerState = rememberTimePickerState(
                initialHour = editDeadline?.hour ?: 12,
                initialMinute = editDeadline?.minute ?: 0
            )
            var showTimePicker by remember { mutableStateOf(false) }
            
            if (!showTimePicker) {
                DatePickerDialog(
                    onDismissRequest = { showEditDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    showTimePicker = true
                                }
                            }
                        ) {
                            Text("Next")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            } else {
                GritDialog(
                    onDismissRequest = { 
                        showEditDatePicker = false
                        showTimePicker = false
                    }
                ) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    TimePicker(state = timePickerState)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { 
                                showEditDatePicker = false
                                showTimePicker = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { dateMillis ->
                                    val date = java.time.Instant.ofEpochMilli(dateMillis)
                                        .atZone(java.time.ZoneOffset.systemDefault())
                                        .toLocalDate()
                                    editDeadline = LocalDateTime.of(
                                        date,
                                        java.time.LocalTime.of(
                                            timePickerState.hour,
                                            timePickerState.minute
                                        )
                                    )
                                }
                                showEditDatePicker = false
                                showTimePicker = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Set")
                        }
                    }
                }
            }
        }
    }

    // add sheet
    if (showTaskAddSheet && state.currentCategory != null) {
        var newTask by remember { mutableStateOf("") }
        var newTaskCategoryId by remember { mutableLongStateOf(state.currentCategory.id) }
        var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
        var selectedDeadline by remember { mutableStateOf<LocalDateTime?>(null) }
        var showDatePicker by remember { mutableStateOf(false) }
        
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        GritBottomSheet(
            onDismissRequest = {
                focusRequester.freeFocus()
                keyboardController?.hide()
                showTaskAddSheet = false
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )

            Text(
                text = stringResource(id = R.string.add_task),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            FlowRow(
                horizontalArrangement = Arrangement.Center
            ) {
                state.tasks.keys.forEach { category ->
                    ToggleButton(
                        checked = category.id == newTaskCategoryId,
                        onCheckedChange = { newTaskCategoryId = category.id },
                        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                        modifier = Modifier.padding(horizontal = 4.dp),
                        content = { Text(category.name) }
                    )
                }
            }

            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text("Task") },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.None
                ),
                keyboardActions = KeyboardActions(
                    onAny = {
                        newTask.plus("\n")
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            
            // Priority selection
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                TaskPriority.values().forEachIndexed { index, priority ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = TaskPriority.values().size
                        ),
                        onClick = { selectedPriority = priority },
                        selected = selectedPriority == priority,
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = when (priority) {
                                TaskPriority.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                TaskPriority.LOW -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    ) {
                        Text(priority.name)
                    }
                }
            }
            
            // Deadline selection
            Text(
                text = "Deadline (Optional)",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedDeadline?.format(
                        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                    ) ?: "Set deadline"
                )
            }
            
            if (selectedDeadline != null) {
                TextButton(
                    onClick = { selectedDeadline = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear deadline")
                }
            }

            Button(
                onClick = {
                    showTaskAddSheet = false

                    onAction(
                        TaskPageAction.UpsertTask(
                            Task(
                                categoryId = newTaskCategoryId,
                                title = newTask,
                                status = false,
                                index = state.tasks[state.currentCategory]?.size ?: 0,
                                priority = selectedPriority,
                                deadline = selectedDeadline
                            )
                        )
                    )
                },
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = newTask.isNotEmpty() && newTask.length <= 100
            ) {
                Text(
                    text = stringResource(id = R.string.add_task),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        
        // Date picker dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            val timePickerState = rememberTimePickerState()
            var showTimePicker by remember { mutableStateOf(false) }
            
            if (!showTimePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    showTimePicker = true
                                }
                            }
                        ) {
                            Text("Next")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            } else {
                GritDialog(
                    onDismissRequest = { 
                        showDatePicker = false
                        showTimePicker = false
                    }
                ) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    TimePicker(state = timePickerState)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { 
                                showDatePicker = false
                                showTimePicker = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { dateMillis ->
                                    val date = java.time.Instant.ofEpochMilli(dateMillis)
                                        .atZone(java.time.ZoneOffset.systemDefault())
                                        .toLocalDate()
                                    selectedDeadline = LocalDateTime.of(
                                        date,
                                        java.time.LocalTime.of(
                                            timePickerState.hour,
                                            timePickerState.minute
                                        )
                                    )
                                }
                                showDatePicker = false
                                showTimePicker = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Set")
                        }
                    }
                }
            }
        }
    }
}