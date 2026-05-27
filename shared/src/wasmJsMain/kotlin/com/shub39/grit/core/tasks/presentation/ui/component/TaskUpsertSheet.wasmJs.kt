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
    onDelete: () -> Unit,
    is24Hr: Boolean,
    modifier: Modifier,
    isEditSheet: Boolean,
) {
    var showDateTimePicker by remember { mutableStateOf(false) }

    TaskUpsertSheetContent(
        task = task,
        categories = categories,
        onDismissRequest = onDismissRequest,
        onUpsert = onUpsert,
        onDelete = onDelete,
        is24Hr = is24Hr,
        isEditSheet = isEditSheet,
        notificationPermission = true,
        showDateTimePicker = showDateTimePicker,
        updateDateTimePickerVisibility = { showDateTimePicker = it },
        onPermissionRequest = {},
        modifier = modifier,
    )
}
