package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import com.shub39.grit.core.habits.domain.HabitWithAnalytics
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.utils.now
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.view_week
import grit.shared.core.generated.resources.weekly_progress
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
@Composable
fun WeeklyBooleanHeatMap(
    heatMapState: HeatMapCalendarState,
    onAction: (HabitsAction) -> Unit,
    currentHabit: HabitWithAnalytics,
    primary: Color,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val scope = rememberCoroutineScope()

    AnalyticsCard(
        title = stringResource(Res.string.weekly_progress),
        icon = Res.drawable.view_week,
        modifier = modifier,
        header = {
            Row {
                IconButton(
                    onClick = {
                        scope.launch { heatMapState.animateScrollBy(-100f) }
                    }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_back),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch { heatMapState.animateScrollBy(100f) }
                    }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_forward),
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
                        text = it.yearMonth.format(YearMonth.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            year()
                        }),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            weekHeader = {
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp, top = 2.dp, bottom = 2.dp)
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
                val done = currentHabit.statuses.any { it.date == day.date }
                val validDay = day.date <= today && day.date.dayOfWeek in currentHabit.habit.days

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(30.dp)
                        .clickable(enabled = validDay) {
                            onAction(
                                HabitsAction.InsertStatus(
                                    currentHabit.habit,
                                    day.date
                                )
                            )
                        }
                        .then(
                            if (done) {
                                val donePrevious = currentHabit.statuses.any { it.date == day.date.minusDays(1) }
                                val doneAfter = currentHabit.statuses.any { it.date == day.date.plusDays(1) }

                                Modifier.background(
                                    color = primary.copy(alpha = 0.2f),
                                    shape = if (donePrevious && doneAfter) {
                                        RoundedCornerShape(5.dp)
                                    } else if (donePrevious) {
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            bottomStart = 20.dp,
                                            topEnd = 5.dp,
                                            bottomEnd = 20.dp
                                        )
                                    } else if (doneAfter) {
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            bottomStart = 5.dp,
                                            topEnd = 20.dp,
                                            bottomEnd = 5.dp
                                        )
                                    } else {
                                        RoundedCornerShape(20.dp)
                                    }
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.day.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (done) FontWeight.Bold else FontWeight.Normal,
                        color = if (done) primary
                        else if (!validDay) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}