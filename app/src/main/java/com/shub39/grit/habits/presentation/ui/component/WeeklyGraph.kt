package com.shub39.grit.habits.presentation.ui.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.domain.WeeklyTimePeriod
import com.shub39.grit.habits.domain.WeeklyTimePeriod.Companion.toWeeks
import com.shub39.grit.habits.presentation.HabitsPageAction
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeeklyGraph(
    canSeeContent: Boolean,
    primary: Color,
    onAction: (HabitsPageAction) -> Unit,
    weeklyGraphData: List<Line>,
    modifier: Modifier = Modifier
) {
    var selectedTimePeriod by rememberSaveable { mutableStateOf(WeeklyTimePeriod.WEEKS_8) }

    AnalyticsCard(
        title = stringResource(R.string.weekly_graph),
        icon = Icons.Rounded.AutoGraph,
        canSeeContent = canSeeContent,
        modifier = modifier.heightIn(max = 400.dp),
        onPlusClick = { onAction(HabitsPageAction.OnShowPaywall) }
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
                    Text(text = "${period.toWeeks()} ${stringResource(R.string.weeks)}")
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