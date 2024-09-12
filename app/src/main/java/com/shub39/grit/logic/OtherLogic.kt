package com.shub39.grit.logic

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object OtherLogic {
    @OptIn(ExperimentalMaterial3Api::class)
    fun localToTimePickerState(localTime: LocalDateTime): TimePickerState {
        val hour = localTime.hour
        val minute = localTime.minute
        return TimePickerState(hour, minute, false)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun timePickerStateToLocalDateTime(timePickerState: TimePickerState, date: LocalDate = LocalDate.now()): LocalDateTime {
        return LocalDateTime.of(date, java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
    }

    fun getNextMonday(): LocalDateTime {
        var nextMonday = LocalDateTime.now().plusWeeks(1)
        while (nextMonday.dayOfWeek != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.minusDays(1)
        }
        return nextMonday.withHour(0).withMinute(0)
    }

    fun countConsecutiveDaysBeforeLast(dates: List<LocalDate>): Int {
        if (dates.size < 2) return 0
        val sortedDates = dates.sorted()
        var consecutiveCount = 0
        for (i in sortedDates.size - 2 downTo 0) {
            val currentDate = sortedDates[i]
            val nextDate = sortedDates[i + 1]
            if (ChronoUnit.DAYS.between(currentDate, nextDate) == 1L) {
                consecutiveCount++
            } else {
                break
            }
        }
        return consecutiveCount
    }

    fun countBestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val sortedDates = dates.sorted()
        var maxConsecutive = 0
        var currentConsecutive = 0
        for (i in 1 until sortedDates.size) {
            val previousDate = sortedDates[i - 1]
            val currentDate = sortedDates[i]
            if (ChronoUnit.DAYS.between(previousDate, currentDate) == 1L) {
                currentConsecutive++
            } else {
                if (currentConsecutive > maxConsecutive) {
                    maxConsecutive = currentConsecutive
                }
                currentConsecutive = 1
            }
        }
        if (currentConsecutive > maxConsecutive) {
            maxConsecutive = currentConsecutive
        }
        return maxConsecutive
    }
}