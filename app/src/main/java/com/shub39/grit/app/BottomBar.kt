package com.shub39.grit.app

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

// just a simple Bottombar
@Composable
fun BottomBar(
    currentRoute: BottomAppBarDestination,
    onChange: (BottomAppBarDestination) -> Unit
) {
    NavigationBar(tonalElevation = 8.dp) {
        BottomAppBarDestination.entries.forEachIndexed { _, destination ->

            NavigationBarItem(
                selected = currentRoute == destination,
                onClick = {
                    if (currentRoute != destination) {
                        onChange(destination)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.icon),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = destination.label)) },
                alwaysShowLabel = false
            )
        }
    }
}