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
package com.shub39.grit.shared.ui.components

import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.compose.ConfettiKitState
import io.github.vinceglb.confettikit.compose.rememberConfettiKitState
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import io.github.vinceglb.confettikit.core.emitter.EmitterConfig
import io.github.vinceglb.confettikit.core.models.Shape
import io.github.vinceglb.confettikit.core.models.Size
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/** Presets used by [Confetti]. */
enum class ConfettiPreset {
    /** Short shower of confetti falling from the top edge. */
    Burst,

    /** Full page celebration, used when everything for the day is done. */
    Celebration,
}

/**
 * Holds the currently playing [ConfettiPreset], if any.
 *
 * The confetti view is only composed while a burst is playing, since the underlying animation loop
 * never stops on its own.
 */
@Stable
class ConfettiState internal constructor(internal val kitState: ConfettiKitState) {
    internal var preset: ConfettiPreset? by mutableStateOf(null)
        private set

    /** Plays [preset], restarting it if a burst is already running. */
    fun show(preset: ConfettiPreset) {
        this.preset = preset
        kitState.reset()
    }

    internal fun clear() {
        preset = null
    }
}

@Composable
fun rememberConfettiState(): ConfettiState {
    val kitState = rememberConfettiKitState()

    return remember(kitState) { ConfettiState(kitState) }
}

/**
 * Draws confetti over the content it is composed with, while [state] has an active burst.
 *
 * Meant to be the last child of a [androidx.compose.foundation.layout.Box] so the particles are
 * drawn above the screen content. It doesn't consume any pointer input.
 */
@Composable
fun Confetti(state: ConfettiState, modifier: Modifier = Modifier) {
    val preset = state.preset ?: return
    val colors = confettiColors()
    val shapes = confettiShapes()
    val parties = remember(preset, colors, shapes) { preset.parties(colors, shapes) }

    ConfettiKit(
        parties = parties,
        state = state.kitState,
        onParticleSystemEnded = { _, activeSystems -> if (activeSystems == 0) state.clear() },
        modifier = modifier,
    )
}

/**
 * Celebrates completions on top of the caller's content.
 *
 * A short [ConfettiPreset.Burst] is played whenever an item gets completed, and a
 * [ConfettiPreset.Celebration] when the completion also finishes everything in [relevantIds].
 * Un-completions and the first load of the data never trigger anything.
 *
 * @param completedIds ids of the items that are currently completed.
 * @param relevantIds ids that have to be completed for the celebration, for example the habits that
 *   are due today.
 * @param isLoaded false while the real data is still loading, keeps the first emission as the
 *   baseline instead of firing a burst for it.
 */
@Composable
fun CompletionConfetti(
    completedIds: List<Long>,
    relevantIds: List<Long>,
    isLoaded: Boolean,
    modifier: Modifier = Modifier,
) {
    val confetti = rememberConfettiState()
    var previousCompletedIds by remember { mutableStateOf<Set<Long>?>(null) }

    LaunchedEffect(completedIds, relevantIds, isLoaded) {
        if (!isLoaded) return@LaunchedEffect

        val completed = completedIds.toSet()
        val previous = previousCompletedIds
        previousCompletedIds = completed

        // first loaded emission is the baseline, and the delta has to be positive
        if (previous == null) return@LaunchedEffect
        if ((completed - previous).isEmpty()) return@LaunchedEffect

        val relevant = relevantIds.toSet()

        confetti.show(
            if (relevant.isNotEmpty() && relevant.all { it in completed }) {
                ConfettiPreset.Celebration
            } else {
                ConfettiPreset.Burst
            }
        )
    }

    Confetti(state = confetti, modifier = modifier)
}

@Composable
private fun confettiColors(): List<Int> {
    val colorScheme = MaterialTheme.colorScheme

    return remember(colorScheme) {
        listOf(
                colorScheme.primary,
                colorScheme.secondary,
                colorScheme.tertiary,
                colorScheme.primaryContainer,
            )
            .map { it.toArgb() }
    }
}

/**
 * Particle shapes, mixing the classic confetti pieces with the Material expressive shapes.
 *
 * Kept stable with [remember] since the library relies on referential equality for shapes.
 */
@Composable
private fun confettiShapes(): List<Shape> {
    val materialShapes =
        listOf(
            MaterialShapes.Cookie9Sided.toShape(),
            MaterialShapes.Cookie12Sided.toShape(),
            MaterialShapes.Pill.toShape(),
            MaterialShapes.SoftBurst.toShape(),
            MaterialShapes.VerySunny.toShape(),
        )

    return remember(materialShapes) {
        listOf(Shape.Circle, Shape.Square) + materialShapes.map { Shape.CustomShape(it) }
    }
}

private fun ConfettiPreset.parties(colors: List<Int>, shapes: List<Shape>): List<Party> =
    when (this) {
        ConfettiPreset.Burst ->
            listOf(
                topRain(
                    colors = colors,
                    shapes = shapes,
                    duration = 500.milliseconds,
                    perSecond = 120,
                )
            )

        ConfettiPreset.Celebration ->
            listOf(
                sideCannon(
                    colors = colors,
                    shapes = shapes,
                    angle = Angle.RIGHT,
                    x = 0.0,
                    emitter = Emitter(duration = 1.seconds).perSecond(120),
                ),
                sideCannon(
                    colors = colors,
                    shapes = shapes,
                    angle = Angle.LEFT,
                    x = 1.0,
                    emitter = Emitter(duration = 1.seconds).perSecond(120),
                ),
                // explosion from the middle
                Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = Spread.ROUND,
                    colors = colors,
                    shapes = shapes,
                    timeToLive = 2500,
                    emitter = Emitter(duration = 200.milliseconds).max(120),
                ),
                // rain from the top edge
                topRain(
                    colors = colors,
                    shapes = shapes,
                    duration = 1200.milliseconds,
                    perSecond = 80,
                    timeToLive = 2000,
                ),
            )
    }

private fun topRain(
    colors: List<Int>,
    shapes: List<Shape>,
    duration: Duration,
    perSecond: Int,
    timeToLive: Long = 1500,
): Party =
    Party(
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        speed = 0f,
        maxSpeed = 15f,
        damping = 0.92f,
        colors = colors,
        shapes = shapes,
        size = listOf(Size.SMALL, Size.MEDIUM),
        timeToLive = timeToLive,
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
        emitter = Emitter(duration = duration).perSecond(perSecond),
    )

private fun sideCannon(
    colors: List<Int>,
    shapes: List<Shape>,
    angle: Int,
    x: Double,
    emitter: EmitterConfig,
): Party =
    Party(
        angle = angle,
        spread = Spread.WIDE,
        speed = 12f,
        maxSpeed = 25f,
        damping = 0.92f,
        colors = colors,
        shapes = shapes,
        size = listOf(Size.SMALL, Size.MEDIUM),
        timeToLive = 1200,
        position = Position.Relative(x, 0.5),
        emitter = emitter,
    )
