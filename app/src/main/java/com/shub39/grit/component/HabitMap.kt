package com.shub39.grit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.habit.DailyHabitStatus
import java.time.LocalDate

@Composable
fun HabitMap(
    data: List<DailyHabitStatus>
) {
    val months = LocalDate.now().minusMonths(3)
    val allDaysOfMonths = generateSequence(months) { it.plusDays(1) }
        .takeWhile { !it.isAfter(LocalDate.now()) }
        .toList()
    val habitDaysSet = data.map { it.date }.toSet()
    val weeks = allDaysOfMonths.chunked(7)

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        weeks.forEach { week ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                week.forEach { day ->
                    DayBox(habitDaysSet.contains(day))
                }
            }
        }
    }
}


@Composable
private fun DayBox(done: Boolean) {
    val color = if (done) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.background
    }
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}