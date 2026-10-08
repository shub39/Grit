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
package com.shub39.grit.shared.ui.habit.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isExpanded
import com.shub39.grit.shared.ui.habit.HabitState
import com.shub39.grit.shared.ui.habit.HabitsAction
import grit.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BoxScope.HabitListFABs(
    onNavigateToOverallAnalytics: () -> Unit,
    state: HabitState,
    fabVisible: Boolean,
    onAction: (HabitsAction) -> Unit,
    onNavigateToPaywall: () -> Unit,
    isUserSubscribed: Boolean,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.isExpanded()

    Row(
        modifier =
            modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .align(if (isExpanded) Alignment.BottomStart else Alignment.BottomEnd),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!isExpanded) {
            AnimatedVisibility(
                visible = state.habitsWithAnalytics.isNotEmpty() && fabVisible,
                enter =
                    fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleIn(MaterialTheme.motionScheme.fastSpatialSpec()),
                exit =
                    fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleOut(MaterialTheme.motionScheme.fastSpatialSpec()),
            ) {
                FloatingActionButton(
                    onClick = onNavigateToOverallAnalytics,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.analytics),
                        contentDescription = "All Analytics",
                    )
                }
            }

            AnimatedVisibility(
                visible = fabVisible,
                enter =
                    fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleIn(MaterialTheme.motionScheme.fastSpatialSpec()),
                exit =
                    fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleOut(MaterialTheme.motionScheme.fastSpatialSpec()),
            ) {
                MediumFloatingActionButton(
                    onClick = { onAction(HabitsAction.OnAddHabitClicked) },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add),
                            contentDescription = "Add Habit",
                            modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize),
                        )

                        AnimatedVisibility(
                            visible = state.habitsWithAnalytics.isEmpty(),
                            enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                            exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                        ) {
                            Text(text = stringResource(Res.string.add_habit))
                        }
                    }
                }
            }
        } else {
            AnimatedVisibility(
                visible = fabVisible,
                enter =
                    fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleIn(MaterialTheme.motionScheme.fastSpatialSpec()),
                exit =
                    fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleOut(MaterialTheme.motionScheme.fastSpatialSpec()),
            ) {
                MediumFloatingActionButton(
                    onClick = {
                        if (isUserSubscribed || state.habitsWithAnalytics.size <= 5) {
                            onAction(HabitsAction.OnAddHabitClicked)
                        } else {
                            onNavigateToPaywall()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add),
                            contentDescription = "Add Habit",
                            modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize),
                        )

                        AnimatedVisibility(
                            visible = state.habitsWithAnalytics.isEmpty(),
                            enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                            exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                        ) {
                            Text(text = stringResource(Res.string.add_habit))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible =
                    state.analyticsHabitId != null &&
                        state.habitsWithAnalytics.isNotEmpty() &&
                        fabVisible,
                enter =
                    fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleIn(MaterialTheme.motionScheme.fastSpatialSpec()),
                exit =
                    fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleOut(MaterialTheme.motionScheme.fastSpatialSpec()),
            ) {
                FloatingActionButton(
                    onClick = onNavigateToOverallAnalytics,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.analytics),
                        contentDescription = "All Analytics",
                    )
                }
            }
        }
    }
}
