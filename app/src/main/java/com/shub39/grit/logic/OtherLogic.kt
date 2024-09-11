package com.shub39.grit.logic

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

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
}