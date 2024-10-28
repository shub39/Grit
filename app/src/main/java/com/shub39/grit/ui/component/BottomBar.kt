package com.shub39.grit.ui.component

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun BottomBar(pagerState: PagerState) {
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