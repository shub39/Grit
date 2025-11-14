package com.shub39.grit.core.habits.presentation.ui.sections

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
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.materialkolor.ktx.fixIfDisliked
import com.materialkolor.ktx.harmonize
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.prepareWeekDayDataToBars
import com.shub39.grit.core.habits.presentation.ui.component.HabitHeatMap
import com.shub39.grit.core.habits.presentation.ui.component.WeekDayBreakdown
import com.shub39.grit.core.habits.presentation.ui.component.WeeklyGraph
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.overall_analytics
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class
)
@Composable
fun OverallAnalytics(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val currentMonth = remember { YearMonth.now() }

    val heatMapData = state.overallAnalytics.heatMapData
    val weeklyBreakdownData =
        prepareWeekDayDataToBars(data = state.overallAnalytics.weekDayFrequencyData, lineColor = primary)
    val weeklyGraphData = state.overallAnalytics.weeklyGraphData.map { entry ->
        val habit = entry.key
        val color = Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat()
        ).harmonize(primary, true).fixIfDisliked()

        Line(
            label = habit.title,
            values = entry.value,
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
                    text = stringResource(Res.string.overall_analytics),
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