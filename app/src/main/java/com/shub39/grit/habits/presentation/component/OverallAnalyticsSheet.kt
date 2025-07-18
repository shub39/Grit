package com.shub39.grit.habits.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.shub39.grit.R
import com.shub39.grit.habits.presentation.HabitPageState
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallAnalyticsSheet(
    state: HabitPageState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMonth = remember { YearMonth.now() }

    val heatMapData = prepareHeatMapData(state.habitsWithStatuses)

    val heatMapState = rememberHeatMapCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = state.startingDay
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                painter = painterResource(R.drawable.round_analytics_24),
                contentDescription = "All Analytics"
            )

            Text(
                text = stringResource(R.string.overall_analytics),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnalyticsCard(
                title = stringResource(R.string.habit_map),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                HeatMapCalendar(
                    state = heatMapState,
                    monthHeader = {
                        Box(
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = it.yearMonth.month.getDisplayName(
                                    TextStyle.SHORT_STANDALONE,
                                    Locale.getDefault()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    weekHeader = {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(30.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.large
                                )
                        ) {
                            Text(
                                text = it.name.take(1),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    dayContent = { day, week ->
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(30.dp)
                                .background(
                                    shape = MaterialTheme.shapes.small,
                                    color = heatMapData[day.date]?.let {
                                        MaterialTheme.colorScheme.primary.copy(
                                            alpha = if (state.habitsWithStatuses.isNotEmpty()) {
                                                (it.toFloat() / state.habitsWithStatuses.size).coerceIn(0f, 1f)
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
    }
}