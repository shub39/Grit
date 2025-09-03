package com.shub39.grit.habits.presentation.ui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.materialkolor.ktx.fixIfDisliked
import com.materialkolor.ktx.harmonize
import com.shub39.grit.R
import com.shub39.grit.habits.presentation.HabitPageState
import com.shub39.grit.habits.presentation.HabitsPageAction
import com.shub39.grit.habits.presentation.prepareHeatMapData
import com.shub39.grit.habits.presentation.prepareLineChartData
import com.shub39.grit.habits.presentation.prepareWeekDayData
import com.shub39.grit.habits.presentation.ui.component.HabitHeatMap
import com.shub39.grit.habits.presentation.ui.component.WeekDayBreakdown
import com.shub39.grit.habits.presentation.ui.component.WeeklyGraph
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.YearMonth
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverallAnalytics(
    state: HabitPageState,
    onAction: (HabitsPageAction) -> Unit,
    onNavigateBack: () -> Unit
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
                radius = 7.dp
            ),
            firstGradientFillColor = color.copy(alpha = 0.8f),
            secondGradientFillColor = Color.Transparent,
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        MediumFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
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
                HabitHeatMap(
                    heatMapState = heatMapState,
                    heatMapData = heatMapData,
                    state = state
                )
            }

            item {
                WeekDayBreakdown(
                    canSeeContent = state.isUserSubscribed,
                    onAction = onAction,
                    weekDayData = weeklyBreakdownData,
                    primary = primary
                )
            }

            item {
                WeeklyGraph(
                    canSeeContent = state.isUserSubscribed,
                    primary = primary,
                    onAction = onAction,
                    weeklyGraphData = weeklyGraphData,
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}