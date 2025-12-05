package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.shub39.grit.core.habits.presentation.HabitState
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.habit_map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import org.jetbrains.compose.resources.stringResource

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun HabitHeatMap(
    heatMapState: HeatMapCalendarState,
    heatMapData: Map<LocalDate, Int>,
    state: HabitState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    AnalyticsCard(
        title = stringResource(Res.string.habit_map),
        icon = Icons.Rounded.Map,
        modifier = modifier.heightIn(max = 400.dp),
        header = {
            Row {
                IconButton(
                    onClick = {
                        scope.launch { heatMapState.animateScrollBy(-100f) }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch { heatMapState.animateScrollBy(100f) }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
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
                        text = it.yearMonth.format(
                            YearMonth.Format {
                                monthName(MonthNames.ENGLISH_ABBREVIATED)
                            }
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