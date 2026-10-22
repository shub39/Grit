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
package com.shub39.grit.shared.ui.habit

/**
 * Filter for the habits list. [Today] and [All] are virtual and always pinned to the front of the
 * selector, followed by the user defined [Category] entries.
 */
sealed interface HabitFilter {
    /** habits scheduled for today (today's day of week is in the habit's days) */
    data object Today : HabitFilter

    /** every habit */
    data object All : HabitFilter

    data class Category(val categoryId: Long) : HabitFilter
}
