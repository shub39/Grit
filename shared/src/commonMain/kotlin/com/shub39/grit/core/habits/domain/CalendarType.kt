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
package com.shub39.grit.core.habits.domain

import grit.shared.generated.resources.Res
import grit.shared.generated.resources.monthly
import grit.shared.generated.resources.yearly
import org.jetbrains.compose.resources.StringResource

enum class CalendarType {
    MONTH,
    YEAR;

    companion object {
        fun CalendarType.toStringRes(): StringResource {
            return when (this) {
                MONTH -> Res.string.monthly
                YEAR -> Res.string.yearly
            }
        }
    }
}
