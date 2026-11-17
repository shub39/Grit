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
package com.shub39.grit.shared.ui.navigation

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation3.ui.LocalNavAnimatedContentScope

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * Whether shared element and shared bounds transitions are enabled for the current destination.
 *
 * Multi-pane scenes compose more than one pane at the same time, which would register the same
 * shared key twice within a single target state. Those scenes opt out by providing `false`.
 */
val LocalSharedTransitionsEnabled = compositionLocalOf { true }

private const val BOUND_DURATION_MILLIS = 500

/**
 * Bounds transform used by every shared transition in the app. It mirrors the 500ms timing of the
 * navigation transitions in [horizontalTransitionMetadata] and friends.
 */
val gritBoundsTransform: BoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    keyframes {
        durationMillis = BOUND_DURATION_MILLIS
        initialBounds at 0 using ArcMode.ArcLinear using FastOutSlowInEasing
        targetBounds at BOUND_DURATION_MILLIS
    }
}

@Composable
fun Modifier.gritSharedElement(
    key: Any,
    boundsTransform: BoundsTransform = gritBoundsTransform,
): Modifier {
    if (!sharedTransitionsAvailable()) return this

    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    with(sharedTransitionScope) {
        return this@gritSharedElement.sharedElement(
            rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = boundsTransform,
        )
    }
}

@Composable
fun Modifier.gritSharedBounds(
    key: Any,
    boundsTransform: BoundsTransform = gritBoundsTransform,
): Modifier {
    if (!sharedTransitionsAvailable()) return this

    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    with(sharedTransitionScope) {
        return this@gritSharedBounds.sharedBounds(
            rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = boundsTransform,
            resizeMode =
                SharedTransitionScope.ResizeMode.scaleToBounds(
                    contentScale = ContentScale.FillBounds
                ),
        )
    }
}

/**
 * Checks the flag before touching [LocalNavAnimatedContentScope], whose default throws because it
 * is only available inside a `NavDisplay`.
 */
@Composable
private fun sharedTransitionsAvailable(): Boolean =
    LocalSharedTransitionsEnabled.current && LocalSharedTransitionScope.current != null
