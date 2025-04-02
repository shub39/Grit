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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.settings.components.AboutLibraries
import com.shub39.grit.core.presentation.settings.components.AboutPage
import com.shub39.grit.core.presentation.settings.components.Backup
import com.shub39.grit.core.presentation.settings.components.LookAndFeelPage
import com.shub39.grit.core.presentation.settings.components.RootPage

@Composable
fun Settings() = PageFill {
    val navcontroller = rememberNavController()

    NavHost(
        navController = navcontroller,
        startDestination = SettingsRoutes.Root,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it }
            ) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it }
            ) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {
        composable<SettingsRoutes.Root> {
            RootPage(
                onAboutClick = { navcontroller.navigate(SettingsRoutes.About) },
                onLookAndFeelClick = { navcontroller.navigate(SettingsRoutes.LookAndFeel) },
                onBackupClick = { navcontroller.navigate(SettingsRoutes.Backup) },
            )
        }

        composable<SettingsRoutes.About> {
            AboutPage()
        }

        composable<SettingsRoutes.LookAndFeel> {
            LookAndFeelPage()
        }

        composable<SettingsRoutes.Backup> {
            Backup()
        }

        composable<SettingsRoutes.AboutLibraries> {
            AboutLibraries()
        }
    }
}