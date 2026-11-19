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
package com.shub39.grit

import com.shub39.grit.core.habits.Habit
import com.shub39.grit.core.habits.HabitCategory
import com.shub39.grit.core.habits.HabitRepo
import com.shub39.grit.core.habits.HabitStatus
import com.shub39.grit.core.now
import com.shub39.grit.core.tasks.Category
import com.shub39.grit.core.tasks.CategoryColors
import com.shub39.grit.core.tasks.SubTask
import com.shub39.grit.core.tasks.Task
import com.shub39.grit.core.tasks.TaskRepo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus

suspend fun seedDesktopDefaults(habitRepo: HabitRepo, taskRepo: TaskRepo) {
    if (taskRepo.getCategories().isEmpty()) {
        taskRepo.upsertCategory(
            Category(name = "Personal", index = 0, color = CategoryColors.BLUE.color)
        )
        taskRepo.upsertCategory(
            Category(name = "Work", index = 1, color = CategoryColors.RED.color)
        )
        taskRepo.upsertCategory(
            Category(name = "Health", index = 2, color = CategoryColors.GREEN.color)
        )
    }

    val taskCategories = taskRepo.getCategories()
    val personalCatId = taskCategories.find { it.name == "Personal" }?.id ?: 0L
    val workCatId = taskCategories.find { it.name == "Work" }?.id ?: 0L
    val healthCatId = taskCategories.find { it.name == "Health" }?.id ?: 0L

    if (taskRepo.getTasks().isEmpty()) {
        if (workCatId != 0L) {
            val workTaskId1 =
                taskRepo.upsertTask(
                    Task(
                        categoryId = workCatId,
                        title = "Review project architecture",
                        index = 0,
                        status = false,
                    )
                )
            if (workTaskId1 != 0L) {
                taskRepo.upsertSubTask(
                    SubTask(
                        taskId = workTaskId1,
                        title = "Analyze database schema",
                        index = 0,
                        status = true,
                    )
                )
                taskRepo.upsertSubTask(
                    SubTask(
                        taskId = workTaskId1,
                        title = "Review dependency injection setup",
                        index = 1,
                        status = false,
                    )
                )
            }

            val workTaskId2 =
                taskRepo.upsertTask(
                    Task(
                        categoryId = workCatId,
                        title = "Prepare quarterly roadmap presentation",
                        index = 1,
                        status = false,
                    )
                )
            if (workTaskId2 != 0L) {
                taskRepo.upsertSubTask(
                    SubTask(
                        taskId = workTaskId2,
                        title = "Gather performance metrics",
                        index = 0,
                        status = true,
                    )
                )
                taskRepo.upsertSubTask(
                    SubTask(
                        taskId = workTaskId2,
                        title = "Create slide deck",
                        index = 1,
                        status = false,
                    )
                )
            }
        }

        if (personalCatId != 0L) {
            val personalTaskId1 =
                taskRepo.upsertTask(
                    Task(
                        categoryId = personalCatId,
                        title = "Organize home office",
                        index = 0,
                        status = false,
                    )
                )
            if (personalTaskId1 != 0L) {
                taskRepo.upsertSubTask(
                    SubTask(
                        taskId = personalTaskId1,
                        title = "Cable management",
                        index = 0,
                        status = true,
                    )
                )
                taskRepo.upsertSubTask(
                    SubTask(
                        taskId = personalTaskId1,
                        title = "Clean desk surface",
                        index = 1,
                        status = false,
                    )
                )
            }

            taskRepo.upsertTask(
                Task(
                    categoryId = personalCatId,
                    title = "Plan weekend hiking trip",
                    index = 1,
                    status = true,
                )
            )
        }

        if (healthCatId != 0L) {
            taskRepo.upsertTask(
                Task(
                    categoryId = healthCatId,
                    title = "Schedule annual dental checkup",
                    index = 0,
                    status = false,
                )
            )
            taskRepo.upsertTask(
                Task(
                    categoryId = healthCatId,
                    title = "Complete 30-minute cardio workout",
                    index = 1,
                    status = true,
                )
            )
        }
    }

    var habitCategories = habitRepo.getHabitCategories()
    if (habitCategories.isEmpty()) {
        habitRepo.upsertHabitCategory(
            HabitCategory(name = "Health & Fitness", index = 0, color = CategoryColors.GREEN.color)
        )
        habitRepo.upsertHabitCategory(
            HabitCategory(name = "Productivity", index = 1, color = CategoryColors.BLUE.color)
        )
        habitRepo.upsertHabitCategory(
            HabitCategory(name = "Mindfulness", index = 2, color = CategoryColors.YELLOW.color)
        )
        habitCategories = habitRepo.getHabitCategories()
    }

    val habitHealthCatId = habitCategories.find { it.name == "Health & Fitness" }?.id
    val habitProdCatId = habitCategories.find { it.name == "Productivity" }?.id
    val habitMindCatId = habitCategories.find { it.name == "Mindfulness" }?.id

    if (habitRepo.getHabits().isEmpty()) {
        val now = LocalDateTime.now()
        val allDays = DayOfWeek.entries.toSet()
        val weekdays = DayOfWeek.entries.toSet() - setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val meditationId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Morning Meditation",
                    description = "10 minutes of mindful breathing",
                    time = now,
                    days = allDays,
                    index = 0,
                    reminder = false,
                    targetValue = 10.0,
                    targetUnit = "mins",
                    categoryId = habitMindCatId,
                )
            )

        val makeBedId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Make the Bed",
                    description = "Start the day with a small win",
                    time = now,
                    days = allDays,
                    index = 1,
                    reminder = false,
                    categoryId = habitProdCatId,
                )
            )

        val waterId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Drink Water",
                    description = "Stay hydrated throughout the day",
                    time = now,
                    days = allDays,
                    index = 2,
                    reminder = false,
                    targetValue = 2000.0,
                    targetUnit = "ml",
                    categoryId = habitHealthCatId,
                )
            )

        val journalId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Journal",
                    description = "Write a few lines about the day",
                    time = now,
                    days = allDays,
                    index = 3,
                    reminder = false,
                    categoryId = habitMindCatId,
                )
            )

        val walkId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Daily Walk",
                    description = "Get at least 30 mins walk",
                    time = now,
                    days = allDays,
                    index = 4,
                    reminder = false,
                    targetValue = 30.0,
                    targetUnit = "mins",
                    categoryId = habitHealthCatId,
                )
            )

        val stretchId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Stretch",
                    description = "Loosen up your back and shoulders",
                    time = now,
                    days = allDays,
                    index = 5,
                    reminder = false,
                    categoryId = habitHealthCatId,
                )
            )

        val planTomorrowId =
            habitRepo.upsertHabit(
                Habit(
                    title = "Plan Tomorrow",
                    description = "Pick the three things that matter most",
                    time = now,
                    days = weekdays,
                    index = 6,
                    reminder = false,
                    categoryId = habitProdCatId,
                )
            )

        if (habitRepo.getHabitStatuses().isEmpty()) {
            val today = LocalDate.now()

            for (daysAgo in 0..90) {
                val date = today.minus(daysAgo, DateTimeUnit.DAY)

                if (meditationId != 0L && daysAgo % 6 != 2) {
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = meditationId, date = date, value = 10.0)
                    )
                }

                if (makeBedId != 0L && daysAgo % 4 != 2) {
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = makeBedId, date = date, value = 1.0)
                    )
                }

                if (waterId != 0L && daysAgo % 7 != 3) {
                    val waterVal = if (daysAgo % 5 == 0) 1800.0 else 2000.0
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = waterId, date = date, value = waterVal)
                    )
                }

                if (journalId != 0L && daysAgo % 3 != 0) {
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = journalId, date = date, value = 1.0)
                    )
                }

                if (walkId != 0L && daysAgo % 7 != 0) {
                    val walkVal = if (daysAgo % 3 == 0) 45.0 else 30.0
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = walkId, date = date, value = walkVal)
                    )
                }

                if (stretchId != 0L && daysAgo % 5 != 4) {
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = stretchId, date = date, value = 1.0)
                    )
                }

                if (planTomorrowId != 0L && date.dayOfWeek in weekdays && daysAgo % 4 != 3) {
                    habitRepo.insertHabitStatus(
                        HabitStatus(habitId = planTomorrowId, date = date, value = 1.0)
                    )
                }
            }
        }
    }
}
