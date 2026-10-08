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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import com.shub39.grit.shared.ui.WindowSize.Companion.isCompact

/** A [Scene] that displays a list and a detail [NavEntry] side-by-side in a 40/60 split. */
data class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f)) { listEntry.Content() }

            // Let the detail entry know not to display a back button.
            CompositionLocalProvider(LocalBackButtonVisibility provides false) {
                Column(modifier = Modifier.weight(0.6f)) {
                    AnimatedContent(
                        targetState = detailEntry,
                        contentKey = { entry -> entry.contentKey },
                        transitionSpec = {
                            slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                        },
                    ) { entry ->
                        entry.Content()
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed in the
         * list pane of a [ListDetailScene].
         */
        fun listPane() = metadata { put(ListKey, true) }

        /**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed in the
         * detail pane of a the [ListDetailScene].
         */
        fun detailPane() = metadata { put(DetailKey, true) }
    }

    object ListKey : NavMetadataKey<Boolean>

    object DetailKey : NavMetadataKey<Boolean>
}

/**
 * This `CompositionLocal` can be used by a detail `NavEntry` to decide whether to display a back
 * button. Default is `true`. It is set to `false` for a detail `NavEntry` when being displayed in a
 * `ListDetailScene`.
 */
val LocalBackButtonVisibility = compositionLocalOf { true }

/**
 * A [SceneStrategy] that returns a [ListDetailScene] if:
 * - the window width is over 600dp
 * - A `Detail` entry is the last item in the back stack
 * - A `List` entry is in the back stack
 *
 * Notably, when the detail entry changes the scene's key does not change. This allows the scene,
 * rather than the NavDisplay, to handle animations when the detail entry changes.
 */
class ListDetailSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {

        if (windowSizeClass.isCompact()) return null

        val listEntry =
            entries.findLast { it.metadata.contains(ListDetailScene.ListKey) } ?: return null
        val detailEntry =
            entries.findLast { it.metadata.contains(ListDetailScene.DetailKey) } ?: return null

        val listIndex = entries.indexOf(listEntry)
        val detailIndex = entries.indexOf(detailEntry)
        if (listIndex >= detailIndex) return null

        // We use the list's contentKey to uniquely identify the scene.
        // This allows the detail panes to be animated in and out by the scene, rather than
        // having NavDisplay animate the whole scene out when the selected detail item changes.
        val sceneKey = listEntry.contentKey

        return ListDetailScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry,
        )
    }
}
