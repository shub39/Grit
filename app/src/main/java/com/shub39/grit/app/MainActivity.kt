package com.shub39.grit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.grit.core.data.GritDatastore
import com.shub39.grit.core.presentation.NotificationMethods.createNotificationChannel
import com.shub39.grit.core.presentation.GritTheme
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        createNotificationChannel(this)

        enableEdgeToEdge()

        setContent {
            val theme by GritDatastore.getTheme(this).collectAsState(initial = "Default")

            GritTheme(theme = theme) {
                // Initialising KoinContext, becuase of log warnings
                KoinContext {
                    Grit()
                }
            }
        }
    }
}