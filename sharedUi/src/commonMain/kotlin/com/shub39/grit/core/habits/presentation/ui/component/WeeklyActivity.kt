/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.habits.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.GritPreviewWrapper
import com.shub39.grit.core.habits.domain.WeeklyTimePeriod
import com.shub39.grit.core.habits.domain.WeeklyTimePeriod.Companion.toWeeks
import grit.sharedui.generated.resources.*
import kotlin.math.roundToInt
import kotlin.random.Random
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeeklyActivity(lineChartData: List<Double>, modifier: Modifier = Modifier) {
    var selectedTimePeriod by rememberSaveable { mutableStateOf(WeeklyTimePeriod.WEEKS_8) }
    val max =
        remember(selectedTimePeriod, lineChartData) {
            lineChartData
                .takeLast(selectedTimePeriod.toWeeks())
                .takeIf { it.any { value -> value != 0.0 } }
                ?.maxOrNull()
        }

    AnalyticsCard(
        title = stringResource(Res.string.weekly_graph),
        icon = Res.drawable.chart_data,
        modifier = modifier.heightIn(max = 400.dp),
    ) {
        if (max != null) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                WeeklyTimePeriod.entries.forEach { period ->
                    ToggleButton(
                        checked = period == selectedTimePeriod,
                        onCheckedChange = { selectedTimePeriod = period },
                        shapes =
                            when (period) {
                                WeeklyTimePeriod.WEEKS_16 ->
                                    ButtonGroupDefaults.connectedLeadingButtonShapes()
                                WeeklyTimePeriod.WEEKS_8 ->
                                    ButtonGroupDefaults.connectedTrailingButtonShapes()
                            },
                        modifier = Modifier.weight(1f),
                        colors =
                            ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                    ) {
                        Text(text = "${period.toWeeks()} ${stringResource(Res.string.weeks)}")
                    }
                }
            }

            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                lineChartData.takeLast(selectedTimePeriod.toWeeks()).forEach { data ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier =
                                Modifier.height(((data.toFloat() / max.toFloat()) * 200).dp)
                                    .fillMaxWidth()
                                    .background(
                                        color =
                                            if (data == max) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary,
                                        shape = CircleShape,
                                    )
                        ) {
                            if (data == max) {
                                Box(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .aspectRatio(1f)
                                            .padding(4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                shape = MaterialShapes.SoftBurst.toShape(),
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (selectedTimePeriod != WeeklyTimePeriod.WEEKS_16) {
                                        Text(
                                            text = data.roundToInt().toString(),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }
                                }
                            } else if (
                                data > (max / 2) && selectedTimePeriod != WeeklyTimePeriod.WEEKS_16
                            ) {
                                Box(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .aspectRatio(1f)
                                            .padding(4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.onSecondary,
                                                shape = MaterialShapes.Circle.toShape(),
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = data.roundToInt().toString(),
                                        color = MaterialTheme.colorScheme.secondary,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else NotEnoughData()
    }
}

@PreviewWrapper(GritPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    WeeklyActivity(lineChartData = (0..15).map { Random.nextDouble(0.0, 7.0) })
}
