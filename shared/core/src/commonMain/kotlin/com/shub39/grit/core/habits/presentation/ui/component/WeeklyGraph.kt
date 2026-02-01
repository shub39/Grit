package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.domain.WeeklyTimePeriod
import com.shub39.grit.core.habits.domain.WeeklyTimePeriod.Companion.toWeeks
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.chart_data
import grit.shared.core.generated.resources.weekly_graph
import grit.shared.core.generated.resources.weeks
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeeklyGraph(
    canSeeContent: Boolean,
    primary: Color,
    weeklyGraphData: List<Line>,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTimePeriod by rememberSaveable { mutableStateOf(WeeklyTimePeriod.WEEKS_8) }

    AnalyticsCard(
        title = stringResource(Res.string.weekly_graph),
        icon = Res.drawable.chart_data,
        canSeeContent = canSeeContent,
        modifier = modifier.heightIn(max = 400.dp),
        onPlusClick = onNavigateToPaywall
    ) {
        LazyRow(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
        ) {
            items(WeeklyTimePeriod.entries) { period ->
                ToggleButton(
                    checked = period == selectedTimePeriod,
                    onCheckedChange = { selectedTimePeriod = period },
                    shapes = when (period) {
                        WeeklyTimePeriod.WEEKS_16 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        WeeklyTimePeriod.WEEKS_8 -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        WeeklyTimePeriod.WEEKS_4 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    },
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Text(text = "${period.toWeeks()} ${stringResource(Res.string.weeks)}")
                }
            }
        }

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
            data = weeklyGraphData.map { line ->
                line.copy(
                    values = line.values.takeLast(selectedTimePeriod.toWeeks())
                )
            },
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            animationMode = AnimationMode.Together(delayBuilder = { it * 500L })
        )
    }
}