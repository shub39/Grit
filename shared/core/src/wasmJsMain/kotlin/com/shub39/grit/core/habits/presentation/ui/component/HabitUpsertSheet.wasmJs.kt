package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shub39.grit.core.habits.domain.Habit

@Composable
actual fun HabitUpsertSheet(
    habit: Habit,
    onDismissRequest: () -> Unit,
    onUpsertHabit: (Habit) -> Unit,
    is24Hr: Boolean,
    modifier: Modifier,
    save: Boolean
) {
    var newHabit by remember { mutableStateOf(habit) }

    HabitUpsertSheetContent(
        newHabit = newHabit,
        updateHabit = { newHabit = it },
        onDismissRequest = onDismissRequest,
        onUpsertHabit = onUpsertHabit,
        is24Hr = is24Hr,
        save = save,
        notificationPermission = true,
        onRequestPermission = {},
        modifier = modifier
    )
}