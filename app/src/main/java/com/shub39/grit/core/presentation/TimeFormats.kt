package com.shub39.grit.core.presentation

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