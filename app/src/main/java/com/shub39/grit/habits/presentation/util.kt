package com.shub39.grit.habits.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.shub39.grit.habits.domain.WeekDayFrequencyData
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.yearMonth

fun prepareWeekDayDataToBars(
    data: WeekDayFrequencyData,
    lineColor: Color
): ImmutableList<Bars> {
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
    }.toImmutableList()
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
            monthName(MonthNames.ENGLISH_FULL)
            char(' ')
            year()
        }
    )

    return "${getOrdinalSuffix(day)} $yearMonth"
}