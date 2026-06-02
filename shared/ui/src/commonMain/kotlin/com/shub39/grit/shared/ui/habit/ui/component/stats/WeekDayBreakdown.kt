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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.WeekDayFrequencyData
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.habit.ui.component.AnalyticsCard
import com.shub39.grit.shared.ui.habit.ui.component.NotEnoughData
import grit.shared.ui.generated.resources.*
import kotlin.random.Random
import kotlin.random.nextInt
import kotlinx.datetime.format.DayOfWeekNames
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeekDayBreakdown(
    canSeeContent: Boolean,
    weekDayData: WeekDayFrequencyData,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val max = weekDayData.values.takeIf { it.any { value -> value != 0 } }?.maxOrNull()

    AnalyticsCard(
        title = stringResource(Res.string.week_breakdown),
        icon = Res.drawable.view_day,
        canSeeContent = canSeeContent,
        onPlusClick = onNavigateToPaywall,
        modifier = modifier.heightIn(min = 200.dp),
    ) {
        if (max != null) {
            Row(
                modifier =
                    Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                weekDayData.forEach { (day, data) ->
                    val height by
                        animateDpAsState(
                            targetValue = ((data.toFloat() / max.toFloat()) * 200).dp,
                            animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                        )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Column(
                            modifier =
                                Modifier.height(height)
                                    .fillMaxWidth()
                                    .background(
                                        color =
                                            if (data == max) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary,
                                        shape = CircleShape,
                                    )
                        ) {
                            AnimatedVisibility(
                                visible = data == max || data > (max / 2),
                                enter = fadeIn(),
                                exit = fadeOut(),
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
                                        Text(
                                            text = data.toString(),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }
                                } else {
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
                                            text = data.toString(),
                                            color = MaterialTheme.colorScheme.secondary,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = day,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                        )
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
    WeekDayBreakdown(
        canSeeContent = true,
        weekDayData =
            DayOfWeekNames.ENGLISH_ABBREVIATED.names.associateWith { Random.nextInt(0..100) },
        onNavigateToPaywall = {},
    )
}
