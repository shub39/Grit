package com.shub39.grit.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.viewModel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSheet(
    habit: Habit,
    vm: HabitViewModel,
    onDismiss: () -> Unit
) {
    val statusList = vm.getHabitStatus(habit.id)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        HabitMap(statusList)

    }
}