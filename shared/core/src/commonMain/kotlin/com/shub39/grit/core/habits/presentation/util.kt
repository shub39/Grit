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
package com.shub39.grit.core.habits.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.shub39.grit.core.habits.domain.WeekDayFrequencyData
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.yearMonth

fun daysStartingFrom(start: DayOfWeek): Set<DayOfWeek> {
    val allDays = DayOfWeek.entries
    val startIndex = start.ordinal

    return buildSet {
        for (i in allDays.indices) {
            add(allDays[(startIndex + i) % allDays.size])
        }
    }
}

fun prepareWeekDayDataToBars(data: WeekDayFrequencyData, lineColor: Color): List<Bars> {
    return data.map { (day, count) ->
        Bars(
            label = day,
            values =
                listOf(
                    Bars.Data(label = day, value = count.toDouble(), color = SolidColor(lineColor))
                ),
        )
    }
}

fun getOrdinalSuffix(day: Int): String {
    return when {
        day in 11..13 -> "${day}th"
        else ->
            when (day % 10) {
                1 -> "${day}st"
                2 -> "${day}nd"
                3 -> "${day}rd"
                else -> "${day}th"
            }
    }
}

fun formatDateWithOrdinal(date: LocalDate): String {
    val day = date.day
    val yearMonth =
        date.yearMonth.format(
            YearMonth.Format {
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                char(' ')
                year()
            }
        )

    return "${getOrdinalSuffix(day)} $yearMonth"
}
