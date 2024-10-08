package com.shub39.grit.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.viewModel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSheet(
    habit: Habit,
    vm: HabitViewModel,
    onDismiss: () -> Unit
) {
    val statusList = vm.getHabitStatus(habit)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = habit.id,
                style = MaterialTheme.typography.titleLarge,
            )

            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.padding(8.dp))

            HabitMap(
                statusList = statusList,
                onClick = { date ->
                    vm.insertHabitStatus(habit, date)
                }
            )
        }
    }
}