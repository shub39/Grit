package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.presentation.HabitsAction
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.week_breakdown
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeekDayBreakdown(
    canSeeContent: Boolean,
    onAction: (HabitsAction) -> Unit,
    weekDayData: List<Bars>,
    primary: Color,
    modifier: Modifier = Modifier
) {
    AnalyticsCard(
        title = stringResource(Res.string.week_breakdown),
        icon = Icons.Rounded.ViewDay,
        canSeeContent = canSeeContent,
        onPlusClick = { onAction(HabitsAction.OnShowPaywall) },
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