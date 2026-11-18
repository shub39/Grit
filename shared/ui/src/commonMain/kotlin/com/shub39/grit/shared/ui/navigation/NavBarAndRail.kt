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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import grit.shared.ui.generated.resources.Res
import grit.shared.ui.generated.resources.Res.string
import grit.shared.ui.generated.resources.alarm
import grit.shared.ui.generated.resources.check_list
import grit.shared.ui.generated.resources.habits
import grit.shared.ui.generated.resources.settings
import grit.shared.ui.generated.resources.tasks
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
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

    NavigationBar(
        containerColor = Color.Transparent,
        modifier =
            modifier.background(
                brush =
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.2f to NavigationBarDefaults.containerColor.copy(0.5f),
                        0.5f to NavigationBarDefaults.containerColor.copy(0.7f),
                        1f to NavigationBarDefaults.containerColor,
                    )
            ),
    ) {
        NAV_ITEMS.forEach { item ->
            val selected = currentRoute.getTopLevelRoute() == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) return@NavigationBarItem

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
