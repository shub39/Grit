package com.shub39.grit.habits.presentation.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.presentation.HabitPageState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HabitHeatMap(
    heatMapState: HeatMapCalendarState,
    heatMapData: Map<LocalDate, Int>,
    state: HabitPageState,
    modifier: Modifier = Modifier
) {
    AnalyticsCard(
        title = stringResource(R.string.habit_map),
        icon = Icons.Rounded.Map,
        modifier = modifier.heightIn(max = 400.dp)
    ) {
        HeatMapCalendar(
            state = heatMapState,
            modifier = Modifier.padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            monthHeader = {
                Box(
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = it.yearMonth.month.getDisplayName(
                            TextStyle.SHORT_STANDALONE,
                            Locale.getDefault()
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            weekHeader = {
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp, top = 2.dp, bottom = 2.dp, end = 4.dp)
                        .size(30.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = it.name.take(1),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            dayContent = { day, _ ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(30.dp)
                        .background(
                            shape = MaterialTheme.shapes.small,
                            color = heatMapData[day.date]?.let {
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = if (state.habitsWithAnalytics.isNotEmpty()) {
                                        (it.toFloat() / state.habitsWithAnalytics.size).coerceIn(
                                            0f,
                                            1f
                                        )
                                    } else {
                                        0.1f
                                    }
                                )
                            } ?: MaterialTheme.colorScheme.background
                        )
                )
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        HabitHeatMap(
            heatMapState = rememberHeatMapCalendarState(),
            heatMapData = emptyMap(),
            state = HabitPageState(),
        )
    }
}