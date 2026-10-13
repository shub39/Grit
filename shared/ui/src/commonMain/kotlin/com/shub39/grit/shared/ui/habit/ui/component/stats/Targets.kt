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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.MeasurableTargets
import com.shub39.grit.core.habits.Target
import com.shub39.grit.core.now
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.habit.ui.component.AnalyticsCard
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun Targets(
    modifier: Modifier = Modifier,
    canSeeContent: Boolean,
    days: Set<DayOfWeek>,
    measurableTargets: MeasurableTargets,
) {
    val today = LocalDate.now()

    AnalyticsCard(
        modifier = modifier,
        canSeeContent = canSeeContent,
        title = stringResource(Res.string.targets),
        icon = Res.drawable.target,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (today.dayOfWeek in days) {
                TargetCard(
                    target = measurableTargets.dailyTarget,
                    title = stringResource(Res.string.daily),
                )
            }
            TargetCard(
                target = measurableTargets.weeklyTarget,
                title = stringResource(Res.string.weekly),
            )
            TargetCard(
                target = measurableTargets.monthlyTarget,
                title = stringResource(Res.string.monthly),
            )
            TargetCard(
                target = measurableTargets.quarterlyTarget,
                title = stringResource(Res.string.quarterly),
            )
            TargetCard(
                target = measurableTargets.yearlyTarget,
                title = stringResource(Res.string.yearly),
            )
        }
    }
}

@Composable
private fun TargetCard(modifier: Modifier = Modifier, target: Target, title: String) {
    val progress = (target.progress / target.target).coerceIn(0.0, 1.0).toFloat()

    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = flexFontRounded()),
            color = colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier.padding(end = 8.dp)
                        .size(64.dp)
                        .background(
                            color = colorScheme.surfaceContainer,
                            shape = MaterialShapes.VerySunny.toShape(),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${(progress * 100).roundToInt()}%",
                    fontFamily = flexFontRounded(),
                    color = colorScheme.onSurface,
                )
            }
            if (progress in 0.01..1.0) {
                Row(
                    modifier =
                        Modifier.weight(progress)
                            .background(
                                color = colorScheme.primary,
                                shape = MaterialTheme.shapes.large,
                            ),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = target.progress.roundToInt().toString(),
                        modifier = Modifier.padding(8.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontFamily = flexFontRounded(),
                        color = colorScheme.onPrimary,
                    )
                }
            }
            if ((1 - progress) in 0.01..1.0) {
                Row(
                    modifier = Modifier.weight(1 - progress),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = target.target.roundToInt().toString(),
                        modifier = Modifier.padding(8.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontFamily = flexFontRounded(),
                        color = colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
@PreviewWrapper(GritPreviewWrapper::class)
private fun Preview() {
    Targets(
        canSeeContent = true,
        days = DayOfWeek.entries.toSet(),
        measurableTargets =
            MeasurableTargets(
                dailyTarget = Target(4.0, 10.0),
                weeklyTarget = Target(63.0, 70.0),
                monthlyTarget = Target(100.0, 300.0),
                quarterlyTarget = Target(200.0, 1200.0),
                yearlyTarget = Target(4000.0, 5200.0),
            ),
    )
}
