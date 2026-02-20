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
package com.shub39.grit.core.data

import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.tasks.domain.Category
import com.shub39.grit.core.tasks.domain.Task
import com.shub39.grit.habits.data.database.HabitEntity
import com.shub39.grit.habits.data.database.HabitStatusEntity
import com.shub39.grit.tasks.data.database.CategoryEntity
import com.shub39.grit.tasks.data.database.TaskEntity

fun HabitEntity.toHabit(): Habit {
    return Habit(
        id = id,
        title = title,
        description = description,
        time = time,
        days = days,
        index = index,
        reminder = reminder,
    )
}

fun HabitStatusEntity.toHabitStatus(): HabitStatus {
    return HabitStatus(id = id, habitId = habitId, date = date)
}

fun Habit.toHabitEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        index = index,
        days = days,
        reminder = reminder,
    )
}

fun HabitStatus.toHabitStatusEntity(): HabitStatusEntity {
    return HabitStatusEntity(id = id, habitId = habitId, date = date)
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        categoryId = categoryId,
        title = title,
        index = index,
        status = status,
        reminder = reminder,
    )
}

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        categoryId = categoryId,
        title = title,
        index = index,
        status = status,
        reminder = reminder,
    )
}

fun CategoryEntity.toCategory(): Category {
    return Category(id = id, name = name, index = index, color = color)
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(id = id, name = name, color = color, index = index)
}
