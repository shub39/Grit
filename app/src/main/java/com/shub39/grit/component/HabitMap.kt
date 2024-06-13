package com.shub39.grit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.habit.DailyHabitStatus
import java.time.LocalDate
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HabitMap(
    data: List<DailyHabitStatus>
) {
    var weeks by remember { mutableStateOf<List<List<LocalDate?>>>(emptyList()) }
    val habitDaysSet = data.map { it.date }.toSet()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(data) {
        weeks = withContext(Dispatchers.Default) {
            val startDate = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).minusWeeks(17)
            val allDays = generateSequence(startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(LocalDate.now()) }
                .toList()

            val daysWithEmptyDays = mutableListOf<LocalDate?>()
            var currentMonth = startDate.monthValue

            for (day in allDays) {
                daysWithEmptyDays.add(day)
                if (day.plusDays(1).monthValue != currentMonth) {
                    currentMonth = day.plusDays(1).monthValue
                    repeat(7) {
                        daysWithEmptyDays.add(null)
                    }
                }
            }
            daysWithEmptyDays.chunked(7)
        }
        coroutineScope.launch {
            listState.scrollToItem(weeks.size - 1)
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        state = listState
    ) {
        items(weeks) { week ->
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                week.forEach { day ->
                    if (day == null) {
                        EmptyDayBox()
                    } else {
                        DayBox(habitDaysSet.contains(day), day)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayBox(
    done: Boolean,
    day: LocalDate
) {
    val color = if (done) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.background
    }
    val textColor = if (done) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            color = textColor
        )
    }
}

@Composable
private fun EmptyDayBox() {
    Box(
        modifier = Modifier
            .size(30.dp)
    )
}
