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
package com.shub39.grit.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

fun verticalTransitionMetadata(durationMillis: Int = 500): Map<String, Any> = metadata {
    put(NavDisplay.TransitionKey) {
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis),
        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
    }
    put(NavDisplay.PopTransitionKey) {
        EnterTransition.None togetherWith
            slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis))
    }
    put(NavDisplay.PredictivePopTransitionKey) {
        EnterTransition.None togetherWith
            slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis))
    }
}

fun horizontalTransitionMetadata(durationMillis: Int = 500): Map<String, Any> = metadata {
    put(NavDisplay.TransitionKey) {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis),
        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
    }
    put(NavDisplay.PopTransitionKey) {
        EnterTransition.None togetherWith
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis))
    }
    put(NavDisplay.PredictivePopTransitionKey) {
        EnterTransition.None togetherWith
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis))
    }
}
