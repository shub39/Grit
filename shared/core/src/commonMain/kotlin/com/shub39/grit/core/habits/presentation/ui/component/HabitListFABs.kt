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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import grit.shared.core.generated.resources.add_habit
import grit.shared.core.generated.resources.analytics
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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

    Row(
        modifier =
            modifier
                .padding(16.dp)
                .align(
                    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                        Alignment.BottomStart
                    } else {
                        Alignment.BottomEnd
                    }
                ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
            FloatingActionButton(
                onClick = onNavigateToOverallAnalytics,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = state.habitsWithAnalytics.isNotEmpty() && fabVisible,
                        alignment = Alignment.BottomEnd,
                    ),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.analytics),
                    contentDescription = "All Analytics",
                )
            }

            MediumFloatingActionButton(
                onClick = { onAction(HabitsAction.OnAddHabitClicked) },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = fabVisible,
                        alignment = Alignment.BottomEnd,
                    ),
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

                    AnimatedVisibility(visible = state.habitsWithAnalytics.isEmpty()) {
                        Text(text = stringResource(Res.string.add_habit))
                    }
                }
            }
        } else {
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
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = fabVisible,
                        alignment = Alignment.BottomEnd,
                    ),
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

                    AnimatedVisibility(visible = state.habitsWithAnalytics.isEmpty()) {
                        Text(text = stringResource(Res.string.add_habit))
                    }
                }
            }

            AnimatedVisibility(visible = state.analyticsHabitId != null) {
                FloatingActionButton(
                    onClick = onNavigateToOverallAnalytics,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier =
                        Modifier.animateFloatingActionButton(
                            visible = state.habitsWithAnalytics.isNotEmpty() && fabVisible,
                            alignment = Alignment.BottomEnd,
                        ),
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
