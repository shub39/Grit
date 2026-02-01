package com.shub39.grit.core.habits.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.habits.presentation.HabitState
import com.shub39.grit.core.habits.presentation.HabitsAction
import com.shub39.grit.core.habits.presentation.ui.component.HabitListFABs
import com.shub39.grit.core.habits.presentation.ui.sections.AnalyticsPage
import com.shub39.grit.core.habits.presentation.ui.sections.HabitsList
import com.shub39.grit.core.habits.presentation.ui.sections.OverallAnalytics
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.utils.LocalWindowSizeClass
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.collapse
import grit.shared.core.generated.resources.completed
import grit.shared.core.generated.resources.expand
import grit.shared.core.generated.resources.habits
import grit.shared.core.generated.resources.reorder
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
private sealed interface HabitRoutes {
    @Serializable
    data object HabitList : HabitRoutes

    @Serializable
    data object HabitAnalytics : HabitRoutes

    @Serializable
    data object OverallAnalytics : HabitRoutes
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsGraph(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    onNavigateToPaywall: () -> Unit,
    isUserSubscribed: Boolean,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalWindowSizeClass.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = HabitRoutes.HabitList,
            enterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
            popExitTransition = { fadeOut(tween(300)) },
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            composable<HabitRoutes.HabitList> {
                Column {
                    HabitsTopAppBar(
                        state = state,
                        onAction = onAction,
                        scrollBehavior = scrollBehavior
                    )

                    PageFill {
                        val lazyListState = rememberLazyListState()
                        val fabVisible by remember {
                            derivedStateOf {
                                lazyListState.firstVisibleItemIndex == 0
                            }
                        }

                        HabitsList(
                            state = state,
                            onAction = onAction,
                            lazyListState = lazyListState,
                            onNavigateToAnalytics = { navController.navigate(HabitRoutes.HabitAnalytics) },
                            modifier = Modifier
                                .fillMaxHeight()
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                        )

                        HabitListFABs(
                            onNavigateToOverallAnalytics = { navController.navigate(HabitRoutes.OverallAnalytics) },
                            state = state,
                            fabVisible = fabVisible,
                            onAction = onAction,
                            onNavigateToPaywall = onNavigateToPaywall,
                            isUserSubscribed = isUserSubscribed
                        )
                    }
                }
            }

            composable<HabitRoutes.HabitAnalytics> {
                AnalyticsPage(
                    state = state,
                    onAction = onAction,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToPaywall = onNavigateToPaywall,
                    isUserSubscribed = isUserSubscribed
                )
            }

            composable<HabitRoutes.OverallAnalytics> {
                OverallAnalytics(
                    state = state,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToPaywall = onNavigateToPaywall,
                    isUserSubscribed = isUserSubscribed
                )
            }
        }
    } else {
        Column(
            modifier = modifier
        ) {
            HabitsTopAppBar(
                state = state,
                onAction = onAction,
                scrollBehavior = scrollBehavior
            )

            Row(
                modifier = Modifier.weight(1f)
            ) {
                Box {
                    val lazyListState = rememberLazyListState()
                    val fabVisible by remember {
                        derivedStateOf {
                            lazyListState.firstVisibleItemIndex == 0
                        }
                    }

                    HabitsList(
                        state = state,
                        onAction = onAction,
                        onNavigateToAnalytics = {},
                        lazyListState = lazyListState,
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = 400.dp)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    )

                    HabitListFABs(
                        onNavigateToOverallAnalytics = { onAction(HabitsAction.PrepareAnalytics(null)) },
                        state = state,
                        fabVisible = fabVisible,
                        onAction = onAction,
                        onNavigateToPaywall = onNavigateToPaywall,
                        isUserSubscribed = isUserSubscribed
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(topStart = 28.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    AnimatedContent(
                        targetState = state.analyticsHabitId
                    ) {
                        if (it != null) {
                            AnalyticsPage(
                                state = state,
                                onAction = onAction,
                                onNavigateBack = { onAction(HabitsAction.PrepareAnalytics(null)) },
                                onNavigateToPaywall = onNavigateToPaywall,
                                isUserSubscribed = isUserSubscribed
                            )
                        } else {
                            OverallAnalytics(
                                state = state,
                                onNavigateBack = {},
                                showNavigateBack = false,
                                onNavigateToPaywall = onNavigateToPaywall,
                                isUserSubscribed = isUserSubscribed
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HabitsTopAppBar(
    state: HabitState,
    onAction: (HabitsAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeFlexibleTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Text(text = stringResource(Res.string.habits))
        },
        subtitle = {
            Column {
                Text(
                    text = "${state.completedHabitIds.size}/${state.habitsWithAnalytics.size} " + stringResource(
                        Res.string.completed
                    )
                )
            }
        },
        actions = {
            AnimatedVisibility(
                visible = state.habitsWithAnalytics.isNotEmpty()
            ) {
                Row {
                    FilledTonalIconToggleButton(
                        checked = state.compactHabitView,
                        shapes = IconToggleButtonShapes(
                            shape = CircleShape,
                            checkedShape = MaterialTheme.shapes.small,
                            pressedShape = MaterialTheme.shapes.extraSmall,
                        ),
                        onCheckedChange = {
                            onAction(
                                HabitsAction.OnToggleCompactView(it)
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (state.compactHabitView) {
                                    Res.drawable.expand
                                } else {
                                    Res.drawable.collapse
                                }
                            ),
                            contentDescription = "Compact View"
                        )
                    }

                    FilledTonalIconToggleButton(
                        checked = state.editState,
                        shapes = IconToggleButtonShapes(
                            shape = CircleShape,
                            checkedShape = MaterialTheme.shapes.small,
                            pressedShape = MaterialTheme.shapes.extraSmall,
                        ),
                        onCheckedChange = { onAction(HabitsAction.OnToggleEditState(it)) },
                        enabled = state.habitsWithAnalytics.isNotEmpty()
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.reorder),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}