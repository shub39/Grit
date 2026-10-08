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
package com.shub39.grit.shared.ui

import androidx.window.core.layout.WindowSizeClass

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun WindowSizeClass.getWindowSize(): WindowSize {
            return if (isAtLeastBreakpoint(840, 900)) {
                EXPANDED
            } else if (isAtLeastBreakpoint(600, 480)) {
                MEDIUM
            } else {
                COMPACT
            }
        }

        fun WindowSizeClass.isExpanded(): Boolean = getWindowSize() == EXPANDED

        fun WindowSizeClass.isCompact(): Boolean = getWindowSize() == COMPACT

        fun WindowSizeClass.isMedium(): Boolean = getWindowSize() == MEDIUM
    }
}
