package com.shub39.grit.ui.component.habitmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.runtime.*
import com.shub39.grit.database.habit.HabitStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HabitMap(
    statusList: List<HabitStatus>,
    onClick: (LocalDate) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var weeks by remember { mutableStateOf<List<List<LocalDate?>>>(emptyList()) }
    val daysSet by remember { mutableStateOf(statusList.map { it.date }.toSet()) }

    LaunchedEffect(daysSet) {
        weeks = withContext(Dispatchers.Default) {
            val startDate = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).minusWeeks(
                17
            )
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

    Card {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .padding(16.dp),
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
                            val containsDay = remember { mutableStateOf(daysSet.contains(day)) }

                            DayBox(
                                done = containsDay.value,
                                day = day,
                                onClick = { onClick(day) }
                            )
                        }
                    }
                }
            }
        }
    }

}
