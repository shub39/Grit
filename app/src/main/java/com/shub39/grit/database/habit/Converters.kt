package com.shub39.grit.database.habit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

}

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