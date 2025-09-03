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
import com.shub39.grit.habits.presentation.HabitsPageAction
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties

@Composable
fun WeeklyGraph(
    canSeeContent: Boolean,
    primary: Color,
    onAction: (HabitsPageAction) -> Unit,
    weeklyGraphData: List<Line>,
    modifier: Modifier = Modifier
) {
    AnalyticsCard(
        title = stringResource(R.string.weekly_graph),
        canSeeContent = canSeeContent,
        modifier = modifier.height(300.dp),
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
            data = weeklyGraphData,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme {
        WeeklyGraph(
            canSeeContent = true,
            primary = MaterialTheme.colorScheme.primary,
            onAction = {  },
            weeklyGraphData = listOf(
                Line(
                    label = "line 1",
                    values = (0..10).map { it.toDouble() },
                    color = SolidColor(MaterialTheme.colorScheme.primary)
                )
            ),
        )
    }
}