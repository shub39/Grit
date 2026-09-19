/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.interfaces

import kotlin.jvm.JvmInline

interface AnalyticsWrapper {
    fun trackEvent(event: String, properties: Map<String, Any>)

    companion object {
        @JvmInline
        value class AnalyticsEvent(val name: String) {
            companion object {
                val APP_OPENED = AnalyticsEvent("app_opened")
                val PAYWALL_OPENED = AnalyticsEvent("paywall_opened")
                val PAYWALL_PURCHASED = AnalyticsEvent("paywall_purchased")

                val TASKS_OPENED = AnalyticsEvent("tasks_opened")
                val TASK_SHEET_OPENED = AnalyticsEvent("task_sheet_opened")
                val TASK_SHEET_DISMISSED = AnalyticsEvent("task_sheet_dismissed")
                val TASK_CREATED = AnalyticsEvent("task_created")
                val TASK_DELETED = AnalyticsEvent("task_deleted")
                val TASK_COMPLETED = AnalyticsEvent("task_completed")
                val TASK_EDITED = AnalyticsEvent("task_edited")

                val TASK_CATEGORY_SHEET_OPENED = AnalyticsEvent("task_category_sheet_opened")
                val TASK_CATEGORY_SHEET_DISMISSED = AnalyticsEvent("task_category_sheet_dismissed")
                val TASK_CATEGORY_CREATED = AnalyticsEvent("task_category_created")
                val TASK_CATEGORY_DELETED = AnalyticsEvent("task_category_deleted")
                val TASK_CATEGORY_EDITED = AnalyticsEvent("task_category_edited")

                val HABITS_OPENED = AnalyticsEvent("habits_opened")
                val HABIT_SHEET_OPENED = AnalyticsEvent("habit_sheet_opened")
                val HABIT_SHEET_DISMISSED = AnalyticsEvent("habit_sheet_dismissed")
                val HABIT_CREATED = AnalyticsEvent("habit_created")
                val HABIT_DELETED = AnalyticsEvent("habit_deleted")
                val HABIT_EDITED = AnalyticsEvent("habit_edited")
                val HABIT_COMPLETED = AnalyticsEvent("habit_completed")
                val HABIT_STATUS_UPDATED = AnalyticsEvent("habit_status_updated")
                val HABIT_ANALYTICS_VIEWED = AnalyticsEvent("habit_analytics_viewed")
                val OVERALL_ANALYTICS_VIEWED = AnalyticsEvent("overall_analytics_viewed")

                val SETTINGS_OPENED = AnalyticsEvent("settings_opened")
                val SETTINGS_UPDATED = AnalyticsEvent("settings_updated")
                val LOOK_AND_FEEL_UPDATED = AnalyticsEvent("look_and_feel_updated")
                val BACKUP_CREATED = AnalyticsEvent("backup_created")
                val BACKUP_RESTORED = AnalyticsEvent("backup_restored")
                val ABOUT_VIEWED = AnalyticsEvent("about_viewed")
                val CHANGELOG_VIEWED = AnalyticsEvent("changelog_viewed")
            }
        }
    }
}
