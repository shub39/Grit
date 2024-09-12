package com.shub39.grit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.runtime.*
import com.shub39.grit.database.habit.Habit
import com.shub39.grit.viewModel.HabitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun HabitMap(
    habit: Habit,
    habitViewModel: HabitViewModel = koinViewModel()
) {
    var weeks by remember { mutableStateOf<List<List<LocalDate?>>>(emptyList()) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val data by remember { mutableStateOf(habitViewModel.getStatusForHabit(habit.id)) }
    val habitDaysSet by remember { mutableStateOf(data.map { it.date }.toSet()) }

    LaunchedEffect(data) {
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

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
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
                        val containsDay = remember { mutableStateOf(habitDaysSet.contains(day)) }

                        DayBox(
                            done = containsDay.value,
                            day = day,
                            onClick = {
                                habitViewModel.addStatusForHabit(habit.id, day.atStartOfDay())
                                containsDay.value = !containsDay.value
                            }
                        )
                    }
                }
            }
        }
    }
}
