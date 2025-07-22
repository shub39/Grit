package com.shub39.grit.habits.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.materialkolor.ktx.fixIfDisliked
import com.materialkolor.ktx.harmonize
import com.shub39.grit.R
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallAnalytics(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateBack : () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val currentMonth = remember { YearMonth.now() }

    val heatMapData = prepareHeatMapData(state.habitsWithStatuses)
    val weeklyBreakdownData =
        prepareWeekDayData(state.habitsWithStatuses.values.flatten().map { it.date }, primary)
    val weeklyGraphData = state.habitsWithStatuses.entries.map { entry ->
        val habit = entry.key
        val statuses = entry.value
        val color = Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat()
        ).harmonize(primary, true).fixIfDisliked()

        Line(
            label = habit.title,
            values = prepareLineChartData(state.startingDay, statuses),
            color = SolidColor(color),
            dotProperties = DotProperties(
                enabled = false,
                color = SolidColor(color),
                strokeWidth = 4.dp,
                radius = 7.dp,
                strokeColor = SolidColor(color.copy(alpha = 0.5f))
            ),
            popupProperties = PopupProperties(
                enabled = false
            ),
            drawStyle = DrawStyle.Stroke(
                width = 3.dp,
                strokeStyle = StrokeStyle.Normal
            )
        )
    }

    val heatMapState = rememberHeatMapCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = state.startingDay
    )


    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.overall_analytics),
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HabitHeatMap(heatMapState, heatMapData, state)
            }

            item {
                WeekDayBreakdown(state, onAction, weeklyBreakdownData, primary)
            }

            item {
                WeeklyGraph(state, primary, onAction, weeklyGraphData)
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun WeekDayBreakdown(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    weeklyBreakdownData: List<Bars>,
    primary: Color
) {
    AnalyticsCard(
        title = stringResource(R.string.week_breakdown),
        isUserSubscribed = state.isUserSubscribed,
        onPlusClick = { onAction(HabitsPageAction.OnShowPaywall) },
        modifier = Modifier.height(300.dp)
    ) {
        RowChart(
            minValue = 0.0,
            maxValue = 7.0,
            data = weeklyBreakdownData,
            dividerProperties = DividerProperties(
                enabled = false
            ),
            popupProperties = PopupProperties(
                enabled = false
            ),
            gridProperties = GridProperties(
                enabled = false
            ),
            indicatorProperties = VerticalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = primary)
            ),
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = primary)
            ),
            labelHelperProperties = LabelHelperProperties(
                enabled = false
            ),
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Circular(10.dp)
            ),
            animationMode = AnimationMode.Together(delayBuilder = { it * 100L })
        )
    }
}

@Composable
private fun WeeklyGraph(
    state: HabitPageState,
    primary: Color,
    onAction: (HabitsPageAction) -> Unit,
    weeklyGraphData: List<Line>
) {
    AnalyticsCard(
        title = stringResource(R.string.weekly_graph),
        isUserSubscribed = state.isUserSubscribed,
        modifier = Modifier.height(300.dp),
        onPlusClick = { onAction(HabitsPageAction.OnShowPaywall) }
    ) {
        LineChart(
            labelHelperProperties = LabelHelperProperties(
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = primary)
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = primary)
            ),
            dividerProperties = DividerProperties(
                xAxisProperties = LineProperties(color = SolidColor(primary)),
                yAxisProperties = LineProperties(color = SolidColor(primary))
            ),
            gridProperties = GridProperties(
                xAxisProperties = GridProperties.AxisProperties(
                    lineCount = 10,
                    color = SolidColor(primary)
                ),
                yAxisProperties = GridProperties.AxisProperties(
                    lineCount = 7,
                    color = SolidColor(primary)
                )
            ),
            data = weeklyGraphData
        )
    }
}

@Composable
private fun HabitHeatMap(
    heatMapState: HeatMapCalendarState,
    heatMapData: Map<LocalDate, Int>,
    state: HabitPageState
) {
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
                                        (it.toFloat() / state.habitsWithStatuses.size).coerceIn(
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