package com.shub39.grit.core.presentation.settings

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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.presentation.settings.ui.section.BackupPage
import com.shub39.grit.core.presentation.settings.ui.section.LookAndFeelPage
import com.shub39.grit.core.presentation.settings.ui.section.RootPage
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.core.presentation.theme.Theme
import com.shub39.grit.core.shared_ui.PageFill
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

private sealed interface SettingsRoutes {
    @Serializable
    data object Root: SettingsRoutes

    @Serializable
    data object LookAndFeel: SettingsRoutes

    @Serializable
    data object Backup: SettingsRoutes

    @Serializable
    data object Server: SettingsRoutes
}

@Composable
fun SettingsGraph(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) = PageFill {
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
                onNavigateToServer = { navController.navigate(SettingsRoutes.Server) }
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
            BackupPage(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.Server> {

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
            onAction = {}
        )
    }
}