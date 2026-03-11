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
import androidx.compose.foundation.shape.CornerSize
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
    return when (streakPosition) {
        StreakPosition.ISOLATED -> CircleShape

        StreakPosition.MIDDLE ->
            if (isFirstDayOfWeek) {
                RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
            } else if (isLastDayOfWeek) {
                RoundedCornerShape(bottomEnd = 4.dp, topEnd = 4.dp)
            } else RoundedCornerShape(0.dp)

        StreakPosition.START ->
            if (isLastDayOfWeek) RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp, topEnd = 4.dp, bottomEnd = 4.dp)
            else RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)

        StreakPosition.END ->
            if (isFirstDayOfWeek) RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp, topStart = 4.dp, bottomStart = 4.dp)
            else RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
    }.let {
        if (streakPosition == StreakPosition.MIDDLE && isFirstDayOfMonth) {
            it.copy(topStart = CornerSize(4.dp), bottomStart = CornerSize(4.dp))
        } else if (streakPosition == StreakPosition.MIDDLE && isLastDayOfMonth) {
            it.copy(topEnd = CornerSize(4.dp), bottomEnd = CornerSize(4.dp))
        } else if (streakPosition == StreakPosition.START && isLastDayOfMonth) {
            it.copy(topEnd = CornerSize(4.dp), bottomEnd = CornerSize(4.dp))
        } else if (streakPosition == StreakPosition.END && isFirstDayOfMonth) {
            it.copy(topStart = CornerSize(4.dp), bottomStart = CornerSize(4.dp))
        } else if (isFirstDayOfMonth) {
            it.copy(topStart = CornerSize(20.dp), bottomStart = CornerSize(20.dp))
        } else if (isLastDayOfMonth) {
            it.copy(topEnd = CornerSize(20.dp), bottomEnd = CornerSize(20.dp))
        } else it
    }
}

fun heatMapStreakShape(
    streakPosition: StreakPosition,
    isFirstDayOfWeek: Boolean,
    isLastDayOfWeek: Boolean,
): Shape {
    return when (streakPosition) {
        StreakPosition.ISOLATED -> CircleShape

        StreakPosition.MIDDLE ->
            if (isFirstDayOfWeek) {
                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            } else if (isLastDayOfWeek) {
                RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
            } else RoundedCornerShape(0.dp)

        StreakPosition.START ->
            if (isLastDayOfWeek) RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp, topStart = 20.dp, topEnd = 20.dp)
            else RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

        StreakPosition.END ->
            if (isFirstDayOfWeek)  RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
            else RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    }
}
