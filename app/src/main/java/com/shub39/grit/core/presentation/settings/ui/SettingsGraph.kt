package com.shub39.grit.core.presentation.settings.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.section.BackupPage
import com.shub39.grit.core.presentation.settings.ui.section.Changelog
import com.shub39.grit.core.presentation.settings.ui.section.LookAndFeelPage
import com.shub39.grit.core.presentation.settings.ui.section.RootPage
import com.shub39.grit.core.shared_ui.PageFill
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.GritTheme
import com.shub39.grit.core.theme.Theme
import kotlinx.serialization.Serializable

private sealed interface SettingsRoutes {
    @Serializable
    data object Root: SettingsRoutes

    @Serializable
    data object LookAndFeel: SettingsRoutes

    @Serializable
    data object Backup: SettingsRoutes

    @Serializable
    data object Changelog: SettingsRoutes
}

@Composable
fun SettingsGraph(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    isUserSubscribed: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
) = PageFill(modifier = modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.Root,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .widthIn(max = 600.dp)
            .fillMaxSize(),
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {
        composable<SettingsRoutes.Root> {
            RootPage(
                state = state,
                onAction = onAction,
                onNavigateToLookAndFeel = { navController.navigate(SettingsRoutes.LookAndFeel) },
                onNavigateToBackup = { navController.navigate(SettingsRoutes.Backup) },
                onNavigateToPaywall = onNavigateToPaywall,
                onNavigateToChangelog = { navController.navigate(SettingsRoutes.Changelog) }
            )
        }

        composable<SettingsRoutes.LookAndFeel> {
            LookAndFeelPage(
                state = state,
                onAction = onAction,
                isUserSubscribed = isUserSubscribed,
                onNavigateToPaywall = onNavigateToPaywall,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.Backup> {
            BackupPage(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.Changelog> {
            Changelog(
                changelog = state.changelog,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        SettingsGraph(
            state = SettingsState(),
            onAction = {},
            onNavigateToPaywall = {},
            isUserSubscribed = true
        )
    }
}