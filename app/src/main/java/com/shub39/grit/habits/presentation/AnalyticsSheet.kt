package com.shub39.grit.habits.presentation

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
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.habits.domain.UILogic
import com.shub39.grit.habits.presentation.component.WeeklyComparisonChart
import com.shub39.grit.habits.presentation.component.habitmap.HabitMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSheet(
    habit: Habit,
    statusList: List<HabitStatus>,
    onDismiss: () -> Unit,
    action: (HabitsPageAction) -> Unit
) {
    var updateTrigger by remember { mutableStateOf(false) }
    var weeklyData by remember { mutableStateOf(UILogic.prepareWeeklyData(statusList)) }

    LaunchedEffect(updateTrigger) {
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
                text = habit.title,
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
                    action(HabitsPageAction.InsertStatus(habit, date))
                    updateTrigger = !updateTrigger
                }
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // needs work, maybe will create my own charting system
            WeeklyComparisonChart(weeklyData)

        }
    }
}