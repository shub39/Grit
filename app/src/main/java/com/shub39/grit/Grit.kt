package com.shub39.grit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.shub39.grit.ui.component.BottomAppBarDestination
import com.shub39.grit.ui.component.BottomBar
import com.shub39.grit.ui.page.SettingsPage
import com.shub39.grit.ui.page.habits_page.HabitsPage
import com.shub39.grit.ui.page.task_page.TaskPage
import com.shub39.grit.viewModel.HabitViewModel
import com.shub39.grit.ui.page.task_page.TaskListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Grit(
    tvm: TaskListViewModel = koinViewModel(),
    hvm: HabitViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState(initialPage = 0) { BottomAppBarDestination.entries.size }

    Scaffold(
        bottomBar = { BottomBar(pagerState) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
        ) { page->
            KoinContext {
                when (page) {
                    0 -> TaskPage(tvm)
                    1 -> HabitsPage(hvm, context = LocalContext.current)
                    2 -> SettingsPage(tvm)
                }
            }
        }
    }
}