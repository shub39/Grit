package com.shub39.grit.habits.presentation.ui.component


import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme
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

@Composable
fun WeeklyActivity(
    primary: Color,
    lineChartData: List<Double>,
    modifier: Modifier = Modifier
) {
    AnalyticsCard(
        title = stringResource(R.string.weekly_graph),
        modifier = modifier.height(300.dp)
    ) {
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
                    label = stringResource(R.string.progress),
                    values = lineChartData,
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

@Preview
@Composable
private fun Preview() {
    GritTheme {
        WeeklyActivity(
            primary = MaterialTheme.colorScheme.primary,
            lineChartData = emptyList()
        )
    }
}