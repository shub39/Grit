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

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/** Max width for single column content (settings, forms) so text lines stay readable. */
val ContentMaxWidth: Dp = 700.dp

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun WindowSizeClass.getWidthSize(): WindowSize {
            return if (isWidthAtLeastBreakpoint(900)) {
                EXPANDED
            } else if (isWidthAtLeastBreakpoint(480)) {
                MEDIUM
            } else {
                COMPACT
            }
        }

        fun WindowSizeClass.getHeightSize(): WindowSize {
            return if (isHeightAtLeastBreakpoint(840)) {
                EXPANDED
            } else if (isHeightAtLeastBreakpoint(600)) {
                MEDIUM
            } else {
                COMPACT
            }
        }

        fun WindowSizeClass.isWidthExpanded(): Boolean = getWidthSize() == EXPANDED

        fun WindowSizeClass.isHeightExpanded(): Boolean = getHeightSize() == EXPANDED

        fun WindowSizeClass.isWidthCompact(): Boolean = getWidthSize() == COMPACT

        fun WindowSizeClass.isHeightCompact(): Boolean = getHeightSize() == COMPACT

        fun WindowSizeClass.isWidthMedium(): Boolean = getWidthSize() == MEDIUM

        fun WindowSizeClass.isHeightMedium(): Boolean = getHeightSize() == MEDIUM

        /** Max width of bottom sheets so they don't stretch across wide windows. */
        fun WindowSizeClass.getSheetMaxWidth(): Dp =
            when (getWidthSize()) {
                COMPACT -> 480.dp
                MEDIUM -> 600.dp
                EXPANDED -> 700.dp
            }

        /** Max height for dialogs and sheets so they don't fill a short window edge to edge. */
        fun WindowSizeClass.getDialogMaxHeight(): Dp =
            when (getHeightSize()) {
                COMPACT -> 560.dp
                MEDIUM -> 640.dp
                EXPANDED -> 720.dp
            }

        /** Minimum width of a single item in the adaptive stats grid. */
        fun WindowSizeClass.getGridMinWidth(): Dp =
            when (getWidthSize()) {
                COMPACT -> 320.dp
                MEDIUM -> 380.dp
                EXPANDED -> 440.dp
            }

        /** Max width of a single stat card so its row content stays readable. */
        fun WindowSizeClass.getStatCardMaxWidth(): Dp =
            when (getWidthSize()) {
                COMPACT -> 280.dp
                MEDIUM -> 300.dp
                EXPANDED -> 340.dp
            }

        /** Max height of the charts inside analytics cards. */
        fun WindowSizeClass.getChartMaxHeight(): Dp =
            when (getHeightSize()) {
                COMPACT -> 300.dp
                MEDIUM -> 380.dp
                EXPANDED -> 440.dp
            }

        /** Height of previews shown inside sheets. */
        fun WindowSizeClass.getPreviewHeight(): Dp =
            when (getHeightSize()) {
                COMPACT -> 220.dp
                MEDIUM -> 265.dp
                EXPANDED -> 320.dp
            }

        /** Edge length of the color picker inside the color picker dialog. */
        fun WindowSizeClass.getColorPickerSize(): Dp =
            when (getWidthSize()) {
                COMPACT -> 280.dp
                MEDIUM -> 320.dp
                EXPANDED -> 360.dp
            }
    }
}
