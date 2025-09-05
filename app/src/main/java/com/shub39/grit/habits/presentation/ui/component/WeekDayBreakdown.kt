package com.shub39.grit.habits.presentation.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.habits.presentation.HabitsPageAction
import com.shub39.grit.habits.presentation.prepareWeekDayData
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import java.time.LocalDate

@Composable
fun WeekDayBreakdown(
    canSeeContent: Boolean,
    onAction: (HabitsPageAction) -> Unit,
    weekDayData: List<Bars>,
    primary: Color,
    modifier: Modifier = Modifier
) {
    AnalyticsCard(
        title = stringResource(R.string.week_breakdown),
        icon = Icons.Rounded.ViewDay,
        canSeeContent = canSeeContent,
        onPlusClick = { onAction(HabitsPageAction.OnShowPaywall) },
        modifier = modifier.heightIn(max = 300.dp)
    ) {
        RowChart(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            data = weekDayData,
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
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            ),
            labelProperties = LabelProperties(
                enabled = true,
                padding = 12.dp,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            ),
            labelHelperProperties = LabelHelperProperties(
                enabled = false
            ),
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Circular(20.dp)
            ),
            animationMode = AnimationMode.Together(delayBuilder = { it * 100L })
        )
    }
}

@Composable
@Preview
private fun Preview() {
    GritTheme {
        WeekDayBreakdown(
            canSeeContent = true,
            onAction = {  },
            weekDayData = prepareWeekDayData(
                dates = (0..10).map {
                    LocalDate.now().minusDays(it.toLong())
                },
                lineColor = MaterialTheme.colorScheme.primary
            ),
            primary = MaterialTheme.colorScheme.primary,
        )
    }
}