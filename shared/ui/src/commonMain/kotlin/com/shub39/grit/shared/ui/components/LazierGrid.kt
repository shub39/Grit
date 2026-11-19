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

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.grit.shared.ui.components.LazierDefaults.lazierEdgeBlur

/**
 * Page level [LazyVerticalStaggeredGrid], the grid counterpart of [LazierColumn]. Draws the app
 * content edge to edge, scrolling underneath the transparent top bar and the overlaid navigation
 * bar.
 *
 * @param contentPadding padding handed over by `Scaffold`.
 * @param extraPadding insets added on top of [contentPadding]. Defaults to
 *   [LazierDefaults.pagePadding], which reserves the window edges and the navigation bar.
 * @param blur which edges to fade the content out at with a progressive blur.
 */
@Composable
fun LazierGrid(
    columns: StaggeredGridCells,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    extraPadding: PaddingValues = LazierDefaults.pagePadding(),
    blur: LazierBlur = LazierBlur.Top,
    reverseLayout: Boolean = false,
    verticalItemSpacing: Dp = 16.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    content: LazyStaggeredGridScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current

    LazyVerticalStaggeredGrid(
        columns = columns,
        state = state,
        contentPadding =
            PaddingValues(
                top = contentPadding.calculateTopPadding() + extraPadding.calculateTopPadding(),
                bottom =
                    contentPadding.calculateBottomPadding() + extraPadding.calculateBottomPadding(),
                start =
                    contentPadding.calculateStartPadding(layoutDirection) +
                        extraPadding.calculateStartPadding(layoutDirection),
                end =
                    contentPadding.calculateEndPadding(layoutDirection) +
                        extraPadding.calculateEndPadding(layoutDirection),
            ),
        reverseLayout = reverseLayout,
        verticalItemSpacing = verticalItemSpacing,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        overscrollEffect = overscrollEffect,
        content = content,
        modifier = modifier.lazierEdgeBlur(blur),
    )
}
