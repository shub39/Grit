package com.shub39.grit

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.grit.database.Datastore
import com.shub39.grit.notification.NotificationMethods.createNotificationChannel
import com.shub39.grit.ui.theme.GritTheme
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        createNotificationChannel(this)

        enableEdgeToEdge()

        setContent {
            val theme by Datastore.getTheme(this).collectAsState(initial = "Default")

            GritTheme(theme = theme) {
                KoinContext {
                    Grit()
                }
            }
        }
    }
}