package com.shub39.grit.shared.ui.navigation

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.window.core.layout.WindowSizeClass

data class ResponsiveNavigationScene<T : Any>(
    private val scene: Scene<T>,
    private val sharedTransitionScope: SharedTransitionScope,
    private val windowSizeClass: WindowSizeClass,
    private val navBarContent: @Composable (() -> Unit),
    private val navRailContent: @Composable (() -> Unit),
) : Scene<T> by scene {
    override val key = scene::class to scene.key

    override val content = @Composable {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        val isMovableContentCaller =
            animatedContentScope.transition.targetState == EnterExitState.Visible

        with(sharedTransitionScope) {
            if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Row(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .cacheSize(!isMovableContentCaller)
                            .sharedElement(
                                rememberSharedContentState("nav-rail"),
                                animatedContentScope
                            )
                    ) {
                        if (isMovableContentCaller) {
                            navRailContent()
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        scene.content()
                    }
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        scene.content()
                    }
                    Box(
                        modifier = Modifier
                            .cacheSize(!isMovableContentCaller)
                            .sharedElement(
                                rememberSharedContentState("nav-bar"),
                                animatedContentScope
                            )
                    ) {
                        if (isMovableContentCaller) {
                            navBarContent()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T : Any> rememberResponsiveNavigationSceneDecoratorStrategy(
    navBar: @Composable () -> Unit,
    navRail: @Composable () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
): ResponsiveNavigationSceneDecoratorStrategy<T> {
    val currentNavBar by rememberUpdatedState(navBar)
    val currentNavRail by rememberUpdatedState(navRail)

    val movableNavBar = remember { movableContentOf { currentNavBar() } }
    val movableNavRail = remember { movableContentOf { currentNavRail() } }

    return remember(windowSizeClass, sharedTransitionScope) {
        ResponsiveNavigationSceneDecoratorStrategy(
            windowSizeClass,
            sharedTransitionScope,
            movableNavBar,
            movableNavRail
        )
    }
}

class ResponsiveNavigationSceneDecoratorStrategy<T : Any>(
    private val windowSizeClass: WindowSizeClass,
    private val sharedTransitionScope: SharedTransitionScope,
    private val navBarContent: @Composable () -> Unit,
    private val navRailContent: @Composable () -> Unit,
) : SceneDecoratorStrategy<T> {

    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return ResponsiveNavigationScene(
            scene,
            sharedTransitionScope,
            windowSizeClass,
            navBarContent,
            navRailContent
        )
    }

}

/**
 * A modifier that caches the measured size of the component and optionally reuses it. For example,
 * this can be used to maintain the size of an element that contains moveable content.
 *
 * @param useCachedSize If true, the modifier will use the previously measured and cached size
 * (if available) instead of the current size. If false, it measures normally and caches the new size.
 */
private fun Modifier.cacheSize(useCachedSize: Boolean): Modifier =
    this.then(CacheSizeElement(useCachedSize))

private data class CacheSizeElement(
    val useCachedSize: Boolean
) : ModifierNodeElement<CacheSizeNode>() {

    override fun create() = CacheSizeNode(useCachedSize)

    override fun update(node: CacheSizeNode) {
        node.useCachedSize = useCachedSize
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "cacheSize"
        properties["useCachedSize"] = useCachedSize
    }
}

private class CacheSizeNode(
    useCachedSize: Boolean
) : Modifier.Node(), LayoutModifierNode {

    var useCachedSize: Boolean = useCachedSize
        set(value) {
            if (field != value) {
                field = value
                invalidateMeasurement()
            }
        }

    private var isSizeCached = false
    private var cachedSize: IntSize = IntSize.Zero

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        val currentSize = IntSize(placeable.width, placeable.height)

        val size = if (useCachedSize && isSizeCached) {
            cachedSize
        } else {
            currentSize
        }

        cachedSize = size
        isSizeCached = true

        return layout(size.width, size.height) {
            placeable.placeRelative(0, 0)
        }
    }
}