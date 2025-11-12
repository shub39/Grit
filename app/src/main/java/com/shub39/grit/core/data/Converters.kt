package com.shub39.grit.core.data

import androidx.room.TypeConverter
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object Converters {
    val allDays = dayOfWeekToString(DayOfWeek.entries.toSet())

    @TypeConverter
    fun dayOfWeekToString(value: Set<DayOfWeek>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun dayOfWeekFromString(value: String): Set<DayOfWeek> {
        return if (value.isBlank()) emptySet() else value.split(",").map { DayOfWeek.valueOf(it) }.toSet()
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun dateFromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { Instant.fromEpochSeconds(value).toLocalDateTime(TimeZone.currentSystemDefault()) }
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toInstant(TimeZone.currentSystemDefault())?.epochSeconds
    }

    @TypeConverter
    fun dayFromTimestamp(value: Long): LocalDate {
        return value.let { LocalDate.fromEpochDays(value) }
    }

    @TypeConverter
    fun dayToTimestamp(date: LocalDate): Long {
        return date.toEpochDays()
    }
}