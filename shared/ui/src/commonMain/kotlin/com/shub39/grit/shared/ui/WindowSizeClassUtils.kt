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