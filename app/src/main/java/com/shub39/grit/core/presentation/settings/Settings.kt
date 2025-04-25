package com.shub39.grit.core.presentation.settings

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.settings.components.AboutLibraries
import com.shub39.grit.core.presentation.settings.components.AboutPage
import com.shub39.grit.core.presentation.settings.components.Backup
import com.shub39.grit.core.presentation.settings.components.LookAndFeelPage
import com.shub39.grit.core.presentation.settings.components.RootPage
import com.shub39.grit.core.presentation.theme.GritTheme

@Composable
fun Settings(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) = PageFill {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.Root,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
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
                onNavigate = { navController.navigate(it) }
            )
        }

        composable<SettingsRoutes.About> {
            AboutPage(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.LookAndFeel> {
            LookAndFeelPage(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.Backup> {
            Backup(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.AboutLibraries> {
            AboutLibraries(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun Preview() {
    GritTheme {
        Settings(
            state = SettingsState(),
            onAction = {}
        )
    }
}