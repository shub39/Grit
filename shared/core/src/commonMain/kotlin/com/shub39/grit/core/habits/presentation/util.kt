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

fun prepareWeekDayDataToBars(
    data: WeekDayFrequencyData,
    lineColor: Color
): List<Bars> {
    return data.map { (day, count) ->
        Bars(
            label = day,
            values = listOf(
                Bars.Data(
                    label = day,
                    value = count.toDouble(),
                    color = SolidColor(lineColor)
                )
            )
        )
    }
}

fun getOrdinalSuffix(day: Int): String {
    return when {
        day in 11..13 -> "${day}th"
        else -> when (day % 10) {
            1 -> "${day}st"
            2 -> "${day}nd"
            3 -> "${day}rd"
            else -> "${day}th"
        }
    }
}

fun formatDateWithOrdinal(date: LocalDate): String {
    val day = date.day
    val yearMonth = date.yearMonth.format(
        YearMonth.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            year()
        }
    )

    return "${getOrdinalSuffix(day)} $yearMonth"
}