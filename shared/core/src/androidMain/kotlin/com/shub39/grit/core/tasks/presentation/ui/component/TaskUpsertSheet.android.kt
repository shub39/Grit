package com.shub39.grit.core.tasks.presentation.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current

    var showDateTimePicker by remember { mutableStateOf(false) }
    var notificationPermission by remember {
        mutableStateOf(
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED) ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        )
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showDateTimePicker = true
            notificationPermission = true
        } else Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
    }

    TaskUpsertSheetContent(
        task = task,
        categories = categories,
        onDismissRequest = onDismissRequest,
        onUpsert = onUpsert,
        is24Hr = is24Hr,
        save = save,
        notificationPermission = notificationPermission,
        showDateTimePicker = showDateTimePicker,
        updateDateTimePickerVisibility = { showDateTimePicker = it },
        onPermissionRequest = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        modifier = modifier
    )
}