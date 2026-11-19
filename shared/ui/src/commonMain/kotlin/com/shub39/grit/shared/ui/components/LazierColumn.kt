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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListDefaults
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.shub39.grit.shared.ui.components.LazierDefaults.lazierEdgeBlur

/**
 * Page level [LazyColumn] that draws the app content edge to edge, scrolling underneath the
 * transparent top bar and the overlaid navigation bar.
 *
 * @param contentPadding padding handed over by `Scaffold`.
 * @param extraPadding insets added on top of [contentPadding]. Defaults to
 *   [LazierDefaults.pagePadding], which reserves the window edges and the navigation bar.
 * @param blur which edges to fade the content out at with a progressive blur.
 */
@Composable
fun LazierColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    extraPadding: PaddingValues = LazierDefaults.pagePadding(),
    blur: LazierBlur = LazierBlur.Top,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    cacheWindow: LazyLayoutCacheWindow = LazyListDefaults.cacheWindow(state),
    content: LazyListScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current

    LazyColumn(
        state = state,
        contentPadding =
            PaddingValues(
                top = contentPadding.calculateTopPadding() + extraPadding.calculateTopPadding(),
                bottom =
                    contentPadding.calculateBottomPadding() + extraPadding.calculateBottomPadding(),
                start =
                    contentPadding.calculateLeftPadding(layoutDirection) +
                        extraPadding.calculateLeftPadding(layoutDirection),
                end =
                    contentPadding.calculateRightPadding(layoutDirection) +
                        extraPadding.calculateRightPadding(layoutDirection),
            ),
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        overscrollEffect = overscrollEffect,
        cacheWindow = cacheWindow,
        content = content,
        modifier = modifier.lazierEdgeBlur(blur),
    )
}
