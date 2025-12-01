package com.shub39.grit.core.tasks.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task

@Composable
actual fun TaskUpsertSheet(
    task: Task,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onUpsert: (Task) -> Unit,
    is24Hr: Boolean,
    modifier: Modifier,
    save: Boolean
) {
    var showDateTimePicker by remember { mutableStateOf(false) }

    TaskUpsertSheetContent(
        task = task,
        categories = categories,
        onDismissRequest = onDismissRequest,
        onUpsert = onUpsert,
        is24Hr = is24Hr,
        save = save,
        notificationPermission = true,
        showDateTimePicker = showDateTimePicker,
        updateDateTimePickerVisibility = { showDateTimePicker = it },
        onPermissionRequest = {},
        modifier = modifier
    )
}