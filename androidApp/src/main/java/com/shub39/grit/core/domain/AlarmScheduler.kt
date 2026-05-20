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
package com.shub39.grit.core.domain

import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.tasks.domain.Task

interface AlarmScheduler {
    fun schedule(habit: Habit)

    fun schedule(task: Task)

    fun cancel(habit: Habit)

    fun cancel(task: Task)

    fun cancelAll()
}
