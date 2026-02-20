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
package com.shub39.grit.core.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

fun LocalDateTime.toFormattedString(is24Hr: Boolean): String {
    return this.format(
        LocalDateTime.Format {
            day()
            char(' ')
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            year()
            chars(" @ ")
            if (is24Hr) hour() else amPmHour()
            char(':')
            minute()
            char(' ')
            if (!is24Hr) amPmMarker(am = "AM", pm = "PM")
        }
    )
}

fun LocalTime.toFormattedString(is24Hr: Boolean): String {
    return this.format(
        LocalTime.Format {
            if (is24Hr) hour() else amPmHour()
            char(':')
            minute()
            char(' ')
            if (!is24Hr) amPmMarker(am = "AM", pm = "PM")
        }
    )
}
