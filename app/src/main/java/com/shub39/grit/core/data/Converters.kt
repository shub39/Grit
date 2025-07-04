package com.shub39.grit.core.data

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

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

    @TypeConverter
    fun datefromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun dayFromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dayToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}