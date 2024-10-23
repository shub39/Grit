package com.shub39.grit.ui.page.habits_page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.logic.UILogic
import com.shub39.grit.ui.page.habits_page.component.WeeklyComparisonChart
import com.shub39.grit.ui.page.habits_page.component.habitmap.HabitMap
import com.shub39.grit.viewModel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSheet(
    habit: Habit,
    vm: HabitViewModel,
    onDismiss: () -> Unit
) {
    var statusList by remember { mutableStateOf(vm.getHabitStatus(habit)) }
    var updateTrigger by remember { mutableStateOf(false) }
    var weeklyData by remember { mutableStateOf<List<Pair<Int, Int>>>(UILogic.prepareWeeklyData(statusList)) }

    LaunchedEffect(updateTrigger) {
        statusList = vm.getHabitStatus(habit)
        weeklyData = UILogic.prepareWeeklyData(statusList)
    }

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
                    updateTrigger = !updateTrigger
                }
            )

            Spacer(modifier = Modifier.padding(8.dp))

            WeeklyComparisonChart(weeklyData)

        }
    }
}