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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** Layout of an [ExpressiveTopBar]. */
enum class ExpressiveTopBarStyle {
    /** Single row, centred title, fixed height. Used by utility screens. */
    CenterAligned,

    /**
     * Two rows with a large title that collapses into a smaller one while the content scrolls. Used
     * by the top level screens together with a [TopAppBarScrollBehavior].
     */
    LargeFlexible,
}

/**
 * The single top bar used by every screen.
 *
 * The bar itself is transparent, so content scrolls underneath it and is faded into the surface by
 * a vertical gradient. Optional [bottomContent] (for example a filter row) is attached to the bar
 * so it stays pinned without introducing a second piece of chrome.
 *
 * @param subtitle optional second line.
 * @param bottomContent optional content rendered directly under the bar, inside the same gradient
 *   and therefore part of the same pinned chrome.
 * @param titleAlignment horizontal alignment of the title and subtitle. Defaults to
 *   [Alignment.Start] for [ExpressiveTopBarStyle.LargeFlexible] and [Alignment.CenterHorizontally]
 *   otherwise. Bars that show [actions] should use [Alignment.Start] so the title does not collide
 *   with them.
 * @param style layout of the bar. Only [ExpressiveTopBarStyle.LargeFlexible] supports collapsing to
 *   a smaller title, so it is the only style that should be given a [scrollBehavior]. On expanded
 *   windows the large layout wastes vertical space, so it falls back to the collapsed bar.
 * @param expandedHeight maximum height of the bar. Leave unspecified to use the Material default
 *   for the chosen [style] and [subtitle].
 * @param maxWidth when set, the bar and its gradient are capped to this width and centred, which
 *   keeps it aligned with a content column that is itself width capped (for example with
 *   `ContentMaxWidth`).
 * @param scrollBehavior when provided, the bar collapses while the content scrolls. The host screen
 *   is responsible for applying `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)`
 *   above this bar.
 */
@Composable
fun ExpressiveTopBar(
    title: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    bottomContent: (@Composable () -> Unit)? = null,
    titleAlignment: Alignment.Horizontal? = null,
    style: ExpressiveTopBarStyle = ExpressiveTopBarStyle.CenterAligned,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    expandedHeight: Dp = Dp.Unspecified,
    maxWidth: Dp? = null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    contentPadding: PaddingValues = TopAppBarDefaults.ContentPadding,
) {
    val alignment =
        titleAlignment
            ?: if (style == ExpressiveTopBarStyle.LargeFlexible) Alignment.Start
            else Alignment.CenterHorizontally

    val useLargeBar = style == ExpressiveTopBarStyle.LargeFlexible
    val barModifier =
        if (maxWidth == null) {
            modifier.background(brush = expressiveTopBarGradient())
        } else {
            modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = maxWidth)
                .background(brush = expressiveTopBarGradient())
        }

    Column(modifier = barModifier) {
        val colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            )

        when {
            useLargeBar ->
                LargeFlexibleTopAppBar(
                    title = title,
                    subtitle = subtitle,
                    titleHorizontalAlignment = alignment,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    expandedHeight = expandedHeight,
                    windowInsets = windowInsets,
                    colors = colors,
                    scrollBehavior = scrollBehavior,
                )

            subtitle == null && alignment == Alignment.CenterHorizontally ->
                CenterAlignedTopAppBar(
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    windowInsets = windowInsets,
                    colors = colors,
                    contentPadding = contentPadding,
                )

            else ->
                TopAppBar(
                    title = title,
                    subtitle = subtitle ?: {},
                    titleHorizontalAlignment = alignment,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    windowInsets = windowInsets,
                    colors = colors,
                    contentPadding = contentPadding,
                )
        }

        bottomContent?.invoke()
    }
}

@Composable
private fun expressiveTopBarGradient(): Brush =
    Brush.verticalGradient(
        0f to colorScheme.surface,
        0.5f to colorScheme.surface.copy(0.7f),
        0.8f to colorScheme.surface.copy(0.5f),
        1f to colorScheme.surface.copy(0f),
    )
