package com.shub39.grit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shub39.grit.database.habit.DailyHabitStatus
import java.time.LocalDate
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HabitMap(
    data: List<DailyHabitStatus>
) {
    var weeks by remember { mutableStateOf<List<List<LocalDate>>>(emptyList()) }
    val habitDaysSet = data.map { it.date }.toSet()
    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(data) {
        weeks = withContext(Dispatchers.Default) {
            val months = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).minusWeeks(17)
            val allDaysOfMonths = generateSequence(months) { it.plusDays(1) }
                .takeWhile { !it.isAfter(LocalDate.now()) }
                .toList()
            allDaysOfMonths.chunked(7)
        }
        coroutineScope.launch {
            listState.scrollToItem(weeks.size - 1)
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(8.dp),
        state = listState
    ) {
        items(weeks) { week ->
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