package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.presentation.prepareWeekDayDataToBars
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.utils.AllPreviews
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.view_day
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
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun WeekDayBreakdown(
    canSeeContent: Boolean,
    weekDayData: List<Bars>,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnalyticsCard(
        title = stringResource(Res.string.week_breakdown),
        icon = Res.drawable.view_day,
        canSeeContent = canSeeContent,
        onPlusClick = onNavigateToPaywall,
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
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            ),
            labelProperties = LabelProperties(
                enabled = true,
                padding = 12.dp,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
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

@AllPreviews
@Composable
private fun Preview() {
    GritTheme {
        WeekDayBreakdown(
            canSeeContent = true,
            weekDayData = prepareWeekDayDataToBars(
                data = DayOfWeek.entries.associate {
                    it.toString().take(3) to Random.nextInt(0..10)
                },
                lineColor = MaterialTheme.colorScheme.primary
            ),
            onNavigateToPaywall = {  },
        )
    }
}