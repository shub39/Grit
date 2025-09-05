package com.shub39.grit.habits.domain


enum class WeeklyTimePeriod {
    WEEKS_16,
    WEEKS_8,
    WEEKS_4;

    companion object {
        fun WeeklyTimePeriod.toWeeks(): Int {
            return when (this) {
                WEEKS_16 -> 16
                WEEKS_8 -> 8
                WEEKS_4 -> 4
            }
        }
    }
}