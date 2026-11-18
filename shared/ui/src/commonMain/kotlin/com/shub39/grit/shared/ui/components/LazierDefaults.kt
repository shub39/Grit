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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isWidthCompact
import com.shub39.grit.shared.ui.progressiveBlurPossible

/** Space reserved by the app chrome that is drawn on top of the page content. */
object PageInsets {
    /** Height of the navigation bar container that overlays the page on compact windows. */
    val NavBarHeight: Dp = 80.dp

    /** Standard gap between page content and the window edges. */
    val Edge: Dp = 16.dp
}

/** Which edges of a lazily laid out page fade out behind the app chrome. */
enum class LazierBlur {
    /** No blur at all. */
    None,

    /** Fade out at the top edge, behind the top bar. */
    Top,

    /** Fade out at the bottom edge, behind the overlaid navigation bar. */
    Bottom,

    /** Fade out at both edges. */
    Both,
}

/** Defaults shared by [LazierColumn] and [LazierGrid]. */
object LazierDefaults {
    private val EdgeBlur: Dp = 24.dp

    /**
     * Bottom space taken by the navigation bar that overlays the page, or zero when the navigation
     * rail is used instead.
     */
    @Composable
    fun navBarClearance(): Dp =
        if (LocalWindowSizeClass.current.isWidthCompact()) PageInsets.NavBarHeight else 0.dp

    /** Edge and top padding, plus clearance for the overlaid navigation bar on compact windows. */
    @Composable
    fun pagePadding(): PaddingValues =
        PaddingValues(
            start = PageInsets.Edge,
            end = PageInsets.Edge,
            top = PageInsets.Edge,
            bottom = PageInsets.Edge + navBarClearance(),
        )

    /**
     * Adds [pagePadding] on top of the padding handed over by `Scaffold`. Use this for scroll
     * containers that are not a [LazierColumn] or [LazierGrid], for example the calendars.
     */
    @Composable
    fun contentPaddingWith(scaffoldPadding: PaddingValues): PaddingValues {
        val direction = LocalLayoutDirection.current
        val page = pagePadding()

        return PaddingValues(
            start =
                scaffoldPadding.calculateLeftPadding(direction) +
                    page.calculateLeftPadding(direction),
            end =
                scaffoldPadding.calculateRightPadding(direction) +
                    page.calculateRightPadding(direction),
            top = scaffoldPadding.calculateTopPadding() + page.calculateTopPadding(),
            bottom = scaffoldPadding.calculateBottomPadding() + page.calculateBottomPadding(),
        )
    }

    /** Applies the progressive edge blur requested by [blur], if the platform supports it. */
    @Composable
    fun Modifier.lazierEdgeBlur(blur: LazierBlur): Modifier {
        val spec = edgeBlurSpec(blur) ?: return this

        return this.blur(blurRadiusSpec = spec)
    }

    /**
     * Progressive blur that fades the scrolling content out at the requested edges. Returns null
     * when no blur is requested or when the platform cannot render progressive blur.
     */
    @Composable
    fun edgeBlurSpec(edges: LazierBlur): BlurRadiusSpec? {
        if (edges == LazierBlur.None || !progressiveBlurPossible()) return null

        val isCompact = LocalWindowSizeClass.current.isWidthCompact()
        val blurTop = edges == LazierBlur.Top || edges == LazierBlur.Both
        // Without the navigation bar there is nothing at the bottom edge to fade into.
        val blurBottom = isCompact && (edges == LazierBlur.Bottom || edges == LazierBlur.Both)

        return when {
            blurTop && blurBottom ->
                BlurRadiusSpec.verticalGradient(
                    listOf(
                        BlurStop(0f, EdgeBlur),
                        BlurStop(0.15f, 0.dp),
                        BlurStop(0.85f, 0.dp),
                        BlurStop(1f, EdgeBlur),
                    )
                )

            blurTop ->
                BlurRadiusSpec.verticalGradient(
                    listOf(BlurStop(0f, EdgeBlur), BlurStop(0.25f, 0.dp), BlurStop(1f, 0.dp))
                )

            blurBottom ->
                BlurRadiusSpec.verticalGradient(
                    listOf(BlurStop(0f, 0.dp), BlurStop(0.85f, 0.dp), BlurStop(1f, EdgeBlur))
                )

            else -> null
        }
    }
}
