package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
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
import grit.shared.core.generated.resources.progress
import grit.shared.core.generated.resources.weekly_graph
import grit.shared.core.generated.resources.weeks
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeeklyActivity(
    primary: Color,
    lineChartData: List<Double>,
    modifier: Modifier = Modifier
) {
    var selectedTimePeriod by rememberSaveable { mutableStateOf(WeeklyTimePeriod.WEEKS_8) }

    AnalyticsCard(
        title = stringResource(Res.string.weekly_graph),
        icon = Icons.Rounded.AutoGraph,
        modifier = modifier.heightIn(max = 400.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = ButtonGroupDefaults.HorizontalArrangement
        ) {
            items(WeeklyTimePeriod.entries) { period ->
                ToggleButton(
                    checked = period == selectedTimePeriod,
                    onCheckedChange = { selectedTimePeriod = period }
                ) {
                    Text(text = "${period.toWeeks()} ${stringResource(Res.string.weeks)}")
                }
            }
        }

        LineChart(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
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
            data = listOf(
                Line(
                    label = stringResource(Res.string.progress),
                    values = lineChartData.takeLast(selectedTimePeriod.toWeeks()),
                    color = SolidColor(primary),
                    dotProperties = DotProperties(
                        enabled = true,
                        color = SolidColor(primary),
                        strokeWidth = 4.dp,
                        radius = 7.dp,
                        strokeColor = SolidColor(primary.copy(alpha = 0.5f))
                    ),
                    popupProperties = PopupProperties(
                        enabled = false
                    ),
                    drawStyle = DrawStyle.Stroke(
                        width = 3.dp,
                        strokeStyle = StrokeStyle.Dashed(
                            intervals = floatArrayOf(
                                10f,
                                10f
                            ), phase = 15f
                        )
                    )
                )
            ),
            maxValue = 7.0,
            minValue = 0.0,
            curvedEdges = false,
            animationMode = AnimationMode.Together(delayBuilder = { it * 500L })
        )
    }
}