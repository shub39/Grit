package com.shub39.grit

import android.os.Build
import androidx.compose.material3.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.grit.component.BottomAppBarDestination
import com.shub39.grit.database.Datastore
import com.shub39.grit.logic.NotificationMethods.createNotificationChannel
import com.shub39.grit.page.AnalyticsPage
import com.shub39.grit.page.TaskPage
import com.shub39.grit.page.HabitsPage
import com.shub39.grit.page.SettingsPage
import com.shub39.grit.ui.theme.GritTheme
import kotlinx.coroutines.launch

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
                val pagerState = rememberPagerState(initialPage = 0) { BottomAppBarDestination.entries.size }
                Scaffold(
                    bottomBar = { BottomBar(pagerState) }
                ) { innerPadding ->
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.padding(innerPadding),
                    ) { page->
                        when (page) {
                            0 -> TaskPage()
                            1 -> HabitsPage(context = this@MainActivity)
                            2 -> AnalyticsPage()
                            3 -> SettingsPage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    NavigationBar(tonalElevation = 8.dp) {
        BottomAppBarDestination.entries.forEachIndexed { index, destination ->
            val isSelected = pagerState.currentPage == index
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconSelected),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = destination.label)) },
                alwaysShowLabel = false
            )
        }
    }
}