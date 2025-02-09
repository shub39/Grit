package com.shub39.grit.core.data.backup

import com.shub39.grit.habits.domain.Habit
import com.shub39.grit.habits.domain.HabitStatus
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.Task
import java.time.Instant
import java.time.ZoneId

fun Habit.toHabitSchema(): HabitSchema {
    return HabitSchema(
        id = id,
        title = title,
        description = description,
        index = index,
        time = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun HabitSchema.toHabit(): Habit {
    return Habit(
        id = id,
        title = title,
        description = description,
        index = index,
        time = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime()
    )
}

fun HabitStatus.toHabitStatusSchema(): HabitStatusSchema {
    return HabitStatusSchema(
        id = id,
        habitId = habitId,
        date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun HabitStatusSchema.toHabitStatus(): HabitStatus {
    return HabitStatus(
        id = id,
        habitId = habitId,
        date = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
    )
}

fun TaskSchema.toTask(): Task {
    return Task(
        id = id,
        categoryId = categoryId,
        title = title,
        status = status,
        index = index
    )
}

fun Task.toTaskSchema(): TaskSchema {
    return TaskSchema(
        id = id,
        categoryId = categoryId,
        title = title,
        status = status,
        index = index
    )
}

fun CategorySchema.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        index = index,
        color = color
    )
}

fun Category.toCategorySchema(): CategorySchema {
    return CategorySchema(
        id = id,
        name = name,
        index = index,
        color = color
    )
}