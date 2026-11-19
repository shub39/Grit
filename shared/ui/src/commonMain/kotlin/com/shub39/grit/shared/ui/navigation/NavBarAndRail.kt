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
package com.shub39.grit.shared.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isWidthCompact
import com.shub39.grit.shared.ui.theme.flexFontRounded
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.Res.string
import grit.shared.ui.generated.resources.alarm
import grit.shared.ui.generated.resources.check_list
import grit.shared.ui.generated.resources.habits
import grit.shared.ui.generated.resources.settings
import grit.shared.ui.generated.resources.tasks
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private data class NavItem(val route: Routes, val title: StringResource, val icon: DrawableResource)

private val NAV_ITEMS =
    listOf(
        NavItem(Tasks, string.tasks, Res.drawable.check_list),
        NavItem(Habits, string.habits, Res.drawable.alarm),
        NavItem(Settings, string.settings, Res.drawable.settings),
    )

@Composable
fun NavBar(modifier: Modifier = Modifier, currentRoute: Routes?, onNavigate: (Routes) -> Unit) {
    if (currentRoute?.getTopLevelRoute() == null) return

    val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()
    val systemBarInsets = WindowInsets.systemBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val isExpanded = !LocalWindowSizeClass.current.isWidthCompact()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = cutoutInsets.calculateStartPadding(layoutDirection),
                    end = cutoutInsets.calculateEndPadding(layoutDirection),
                ),
        contentAlignment = Alignment.Center,
    ) {
        HorizontalFloatingToolbar(
            expanded = true,
            scrollBehavior =
                FloatingToolbarDefaults.exitAlwaysScrollBehavior(
                    FloatingToolbarExitDirection.Bottom
                ),
            colors =
                FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                    toolbarContainerColor = colorScheme.primaryContainer,
                    toolbarContentColor = colorScheme.onPrimaryContainer,
                ),
            modifier =
                Modifier.padding(
                        top = ScreenOffset,
                        bottom = systemBarInsets.calculateBottomPadding() + ScreenOffset,
                    )
                    .zIndex(1f),
        ) {
            NAV_ITEMS.fastForEach { item ->
                val selected = currentRoute.getTopLevelRoute() == item.route

                ToggleButton(
                    checked = selected,
                    onCheckedChange = {
                        if (selected) return@ToggleButton

                        when (item.route) {
                            Tasks -> onNavigate(TaskList)
                            Habits -> onNavigate(HabitsList)
                            Settings -> onNavigate(SettingsHome)
                            else -> {}
                        }
                    },
                    colors =
                        ToggleButtonDefaults.colors(
                            containerColor = colorScheme.primaryContainer,
                            contentColor = colorScheme.onPrimaryContainer,
                            checkedContainerColor = colorScheme.primary,
                            checkedContentColor = colorScheme.onPrimary,
                        ),
                    shapes = ToggleButtonShapes(CircleShape, CircleShape, CircleShape),
                    modifier = Modifier.height(56.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(item.icon), contentDescription = null)
                        AnimatedVisibility(
                            visible = selected || isExpanded,
                            enter = expandHorizontally(motionScheme.defaultSpatialSpec()),
                            exit = shrinkHorizontally(motionScheme.defaultSpatialSpec()),
                        ) {
                            Text(
                                text = stringResource(item.title),
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontFamily = flexFontRounded(),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier.padding(start = ButtonDefaults.IconSpacing),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavRail(modifier: Modifier = Modifier, currentRoute: Routes?, onNavigate: (Routes) -> Unit) {
    if (currentRoute?.getTopLevelRoute() == null) return

    NavigationRail(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        NAV_ITEMS.forEach { item ->
            val selected = currentRoute.getTopLevelRoute() == item.route

            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (selected) return@NavigationRailItem

                    when (item.route) {
                        Tasks -> onNavigate(TaskList)
                        Habits -> onNavigate(HabitsList)
                        Settings -> onNavigate(SettingsHome)
                        else -> {}
                    }
                },
                icon = { Icon(imageVector = vectorResource(item.icon), contentDescription = null) },
                label = { Text(text = stringResource(item.title)) },
                alwaysShowLabel = false,
            )
        }
    }
}
