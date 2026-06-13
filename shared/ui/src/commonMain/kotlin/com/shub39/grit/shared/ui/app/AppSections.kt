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
package com.shub39.grit.shared.ui.app

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import grit.shared.ui.generated.resources.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface AppSections : NavKey {
    @Serializable data object HabitPages : AppSections

    @Serializable data object TaskPages : AppSections

    @Serializable data object SettingsPages : AppSections

    companion object {
        val configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(TaskPages::class, TaskPages.serializer())
                    subclass(HabitPages::class, HabitPages.serializer())
                    subclass(SettingsPages::class, SettingsPages.serializer())
                }
            }
        }

        val mainRoutes: List<AppSections> = listOf(TaskPages, HabitPages, SettingsPages)

        fun AppSections.toStringRes(): StringResource {
            return when (this) {
                HabitPages -> Res.string.habits
                TaskPages -> Res.string.tasks
                SettingsPages -> Res.string.settings
            }
        }

        fun AppSections.toIconRes(): DrawableResource {
            return when (this) {
                HabitPages -> Res.drawable.alarm
                TaskPages -> Res.drawable.check_list
                SettingsPages -> Res.drawable.settings
            }
        }
    }
}
