package com.shub39.grit.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.shub39.grit.database.habit.HabitStatus
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

// common ui logic used globally
object UILogic {
    fun openLinkInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    // prepares data for the graph library
    fun prepareWeeklyData(habitStatusList: List<HabitStatus>): List<Pair<Int, Int>> {
        if (habitStatusList.isEmpty()) return emptyList()

        val weekFields = WeekFields.of(Locale.getDefault())
        val startDate = habitStatusList.minByOrNull { it.date }?.date ?: LocalDate.now()
        val endDate = habitStatusList.maxByOrNull { it.date }?.date ?: LocalDate.now()

        val startWeek = startDate.get(weekFields.weekOfYear())
        val endWeek = endDate.get(weekFields.weekOfYear())

        val habitCompletionByWeek = habitStatusList
            .groupBy { it.date.get(weekFields.weekOfYear()) }
            .mapValues { (_, habitStatuses) -> habitStatuses.size }

        return (startWeek..endWeek).map { week ->
            week to (habitCompletionByWeek[week] ?: 0)
        }
    }
}