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
package com.shub39.grit.shared.ui.habit.ui.component.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.shub39.grit.core.habits.WeeklyTimePeriod
import com.shub39.grit.core.habits.WeeklyTimePeriod.Companion.toDisplayString
import com.shub39.grit.core.habits.WeeklyTimePeriod.Companion.toWeeks
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.habit.ui.component.AnalyticsCard
import com.shub39.grit.shared.ui.habit.ui.component.NotEnoughData
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import kotlin.random.Random
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeeklyActivity(lineChartData: List<Double>, modifier: Modifier = Modifier) {
    var selectedTimePeriod by rememberSaveable { mutableStateOf(WeeklyTimePeriod.MONTHS_2) }
    val max =
        remember(selectedTimePeriod, lineChartData) {
            lineChartData
                .takeLast(selectedTimePeriod.toWeeks())
                .takeIf { it.any { value -> value != 0.0 } }
                ?.maxOrNull()
        }
    val animatedMax by
        animateFloatAsState(targetValue = max?.toFloat() ?: 1f, label = "WeeklyActivityMax")

    AnalyticsCard(
        title = stringResource(Res.string.weekly_graph),
        icon = Res.drawable.chart_data,
        modifier = modifier.heightIn(max = 400.dp),
    ) {
        if (max != null) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                WeeklyTimePeriod.entries.forEach { period ->
                    ToggleButton(
                        checked = period == selectedTimePeriod,
                        onCheckedChange = { selectedTimePeriod = period },
                        shapes =
                            when (period) {
                                MONTHS_2 -> ButtonGroupDefaults.connectedLeadingButtonShapes()

                                YEARS_1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()

                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                        modifier = Modifier.weight(1f),
                        colors =
                            ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                    ) {
                        Text(text = period.toDisplayString())
                    }
                }
            }

            val avg =
                remember(selectedTimePeriod, lineChartData) {
                    lineChartData
                        .takeLast(selectedTimePeriod.toWeeks())
                        .dropLast(1)
                        .average()
                        .fastRoundToInt()
                }

            Column {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(
                                style =
                                    MaterialTheme.typography.displayMedium
                                        .copy(fontFamily = flexFontRounded())
                                        .toSpanStyle()
                            ) {
                                append("$avg ")
                            }

                            withStyle(
                                style =
                                    MaterialTheme.typography.bodyMedium
                                        .copy(fontFamily = flexFontRounded())
                                        .toSpanStyle()
                            ) {
                                append("Completions per week (Avg)")
                            }
                        },
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Row(
                    modifier =
                        Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    val allData =
                        remember(lineChartData) {
                            lineChartData.takeLast(WeeklyTimePeriod.YEARS_1.toWeeks())
                        }
                    allData.forEachIndexed { index, data ->
                        val isVisible = index >= allData.size - selectedTimePeriod.toWeeks()

                        val weight by
                            animateFloatAsState(
                                targetValue = if (isVisible) 1f else 0f,
                                animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                            )
                        val height by
                            animateDpAsState(
                                targetValue = ((data.toFloat() / animatedMax) * 200).dp,
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                            )

                        if (weight > 0f) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier =
                                    Modifier.weight(weight)
                                        .height(height)
                                        .background(
                                            color =
                                                if (data == max) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.secondary,
                                            shape = CircleShape,
                                        )
                                        .animateContentSize(
                                            MaterialTheme.motionScheme.slowSpatialSpec()
                                        ),
                            ) {
                                AnimatedVisibility(
                                    visible =
                                        data == max &&
                                            selectedTimePeriod == WeeklyTimePeriod.MONTHS_2,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                ) {
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
                                        Icon(
                                            painter = painterResource(Res.drawable.check),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
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
