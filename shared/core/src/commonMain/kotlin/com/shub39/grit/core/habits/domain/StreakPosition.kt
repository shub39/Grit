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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

enum class StreakPosition {
    ISOLATED,
    START,
    MIDDLE,
    END,
}

fun calendarMapStreakShape(
    streakPosition: StreakPosition,
    isFirstDayOfWeek: Boolean,
    isLastDayOfWeek: Boolean,
    isFirstDayOfMonth: Boolean,
    isLastDayOfMonth: Boolean,
): Shape {
    if (streakPosition == StreakPosition.ISOLATED) return CircleShape

    // Calculate left-side corners (topStart, bottomStart)
    val leftRadius =
        when {
            streakPosition == StreakPosition.START -> 20.dp
            isFirstDayOfWeek || isFirstDayOfMonth -> 4.dp
            else -> 0.dp
        }

    // Calculate right-side corners (topEnd, bottomEnd)
    val rightRadius =
        when {
            streakPosition == StreakPosition.END -> 20.dp
            isLastDayOfWeek || isLastDayOfMonth -> 4.dp
            else -> 0.dp
        }

    return RoundedCornerShape(
        topStart = leftRadius,
        bottomStart = leftRadius,
        topEnd = rightRadius,
        bottomEnd = rightRadius,
    )
}

fun heatMapStreakShape(
    streakPosition: StreakPosition,
    isFirstDayOfWeek: Boolean,
    isLastDayOfWeek: Boolean,
): Shape {
    if (streakPosition == StreakPosition.ISOLATED) return CircleShape

    // Calculate top-side corners (topStart, topEnd)
    val topRadius =
        when {
            streakPosition == StreakPosition.START -> 20.dp
            isFirstDayOfWeek -> 4.dp
            else -> 0.dp
        }

    // Calculate bottom-side corners (bottomStart, bottomEnd)
    val bottomRadius =
        when {
            streakPosition == StreakPosition.END -> 20.dp
            isLastDayOfWeek -> 4.dp
            else -> 0.dp
        }

    return RoundedCornerShape(
        topStart = topRadius,
        topEnd = topRadius,
        bottomStart = bottomRadius,
        bottomEnd = bottomRadius,
    )
}
