package com.shub39.grit.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.shub39.grit.database.habit.HabitStatus

@Composable
fun HabitMap(
    statusList: List<HabitStatus>,
) {
    Text(statusList.toString())
}
