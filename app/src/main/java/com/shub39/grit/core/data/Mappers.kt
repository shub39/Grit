package com.shub39.grit.core.data

import com.shub39.grit.habits.data.database.HabitEntity
import com.shub39.grit.habits.data.database.HabitStatusEntity
import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.tasks.data.database.CategoryEntity
import com.shub39.grit.tasks.data.database.TaskEntity
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task

fun HabitEntity.toHabit(): Habit {
    return Habit(
        id = id,
        title = title,
        description = description,
        time = time,
        days = days,
        index = index
    )
}

fun HabitStatusEntity.toHabitStatus(): HabitStatus {
    return HabitStatus(
        id = id,
        habitId = habitId,
        date = date
    )
}

fun Habit.toHabitEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        index = index,
        days = days
    )
}

fun HabitStatus.toHabitStatusEntity(): HabitStatusEntity {
    return HabitStatusEntity(
        id = id,
        habitId = habitId,
        date = date
    )
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        categoryId = categoryId,
        title = title,
        index = index,
        status = status,
        deadline = deadline?.atZone(java.time.ZoneOffset.systemDefault())?.toInstant()?.toEpochMilli(),
        priority = priority.name
    )
}

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        categoryId = categoryId,
        title = title,
        index = index,
        status = status,
        deadline = deadline?.let { 
            java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(it), 
                java.time.ZoneOffset.systemDefault()
            )
        },
        priority = com.shub39.grit.tasks.domain.TaskPriority.valueOf(priority)
    )
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        index = index,
        color = color
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        color = color,
        index = index
    )
}