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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.now
import com.shub39.grit.core.toFormattedString
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Component to display overview stats of a habit
 *
 * @param consistency consistency of the habit displayed as a percentage
 * @param startDate date the habit was created
 * @param bestStreak longest consecutive days habit was completed
 * @param currentStreak current streak of completions
 */
@Composable
fun StartStats(
    consistency: Float,
    startDate: LocalDate,
    bestStreak: Int,
    currentStreak: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().widthIn(max = 300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // consistency
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { consistency },
                strokeCap = StrokeCap.Round,
                strokeWidth = 36.dp,
                modifier = Modifier.size(200.dp),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(consistency * 100).roundToInt()}%",
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = flexFontRounded(),
                            color = MaterialTheme.colorScheme.primary,
                        ),
                )
                Text(
                    text = stringResource(Res.string.consistency),
                    style =
                        MaterialTheme.typography.titleSmall.copy(
                            fontFamily = flexFontRounded(),
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
            }
        }

        // stats
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // start date
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                        )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier.wrapContentSize()
                                .background(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                ),
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.flag),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 14.dp),
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(Res.string.started_on),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = flexFontRounded(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                        Text(
                            text = startDate.toFormattedString(),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = flexFontRounded(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(),
                        )
                    }
                }
            }

            // best streak
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                        )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier.wrapContentSize()
                                .background(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                ),
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heat),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 14.dp),
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(Res.string.best_streak),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = flexFontRounded(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                        Text(
                            text = bestStreak.toString(),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = flexFontRounded(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                    }
                }
            }

            // current streak
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                        )
            ) {
                val fraction by
                    animateFloatAsState(
                        targetValue = (currentStreak.toFloat() / bestStreak).coerceIn(0f, 1f),
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    )

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                brush =
                                    Brush.horizontalGradient(
                                        0f to MaterialTheme.colorScheme.primaryContainer,
                                        fraction to MaterialTheme.colorScheme.primaryContainer,
                                        fraction to Color.Transparent,
                                        1f to Color.Transparent,
                                    ),
                            )
                            .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier.wrapContentSize()
                                .background(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                ),
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.sprint),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 14.dp),
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(Res.string.streak),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = flexFontRounded(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                        Text(
                            text = currentStreak.toString(),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = flexFontRounded(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@PreviewWrapper(GritPreviewWrapper::class)
@PreviewLightDark
@Composable
private fun Preview() {
    StartStats(consistency = 0.28f, startDate = LocalDate.now(), bestStreak = 12, currentStreak = 8)
}
