package com.shub39.grit.ui.page.habits_page.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer

// needs work
@Composable
fun WeeklyComparisonChart(
    weeklyData: List<Pair<Int, Int>>
) {
    val primary = MaterialTheme.colorScheme.primary

    AnalyticsCard(title = "Weekly Comparison") {

        LineChart(
            linesChartData = listOf(
                LineChartData(
                    points = weeklyData.takeLast(12).map { (week, completedDays) ->
                        LineChartData.Point(
                            completedDays.toFloat(),
                            week.toString()
                        )
                    },
                    lineDrawer = SolidLineDrawer(
                        color = primary,
                        thickness = 3.dp
                    ),
                    startAtZero = true,
                    padBy = 20f
                )
            ),
            pointDrawer = FilledCircularPointDrawer(
                color = primary
            ),
            yAxisDrawer = SimpleYAxisDrawer(
                labelValueFormatter = { it.toInt().toString() },
                labelTextColor = primary,
                axisLineColor = primary,
                axisLineThickness = 3.dp
            ),
            xAxisDrawer = SimpleXAxisDrawer(
                labelTextColor = primary,
                axisLineColor = primary,
                axisLineThickness = 3.dp
            ),
            horizontalOffset = 8f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, top = 32.dp, bottom = 32.dp)
                .height(300.dp)
        )

    }

}