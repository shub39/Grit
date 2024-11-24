package com.shub39.grit.core.presentation

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.data.GritDatastore
import com.shub39.grit.core.domain.IntentActions
import com.shub39.grit.core.domain.NotificationReceiver
import com.shub39.grit.tasks.presentation.TaskListViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(
    taskListViewModel: TaskListViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentTheme by GritDatastore.getTheme(context).collectAsState(initial = "Default")
    val currentClearPreference by GritDatastore.clearPreferences(context)
        .collectAsState(initial = "Daily")
    var theme = currentTheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    bottomStart = 8.dp,
                    topEnd = 18.dp,
                    bottomEnd = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.material_theme))
                    Switch(
                        checked = currentTheme == "Material",
                        onCheckedChange = {
                            if (theme != "Material") {
                                theme = "Material"
                                coroutineScope.launch {
                                    GritDatastore.setTheme(context, "Material")
                                }
                            } else {
                                theme = "Default"
                                coroutineScope.launch {
                                    GritDatastore.setTheme(context, "Default")
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Card(
            shape = RoundedCornerShape(
                topStart = 8.dp,
                bottomStart = 18.dp,
                topEnd = 8.dp,
                bottomEnd = 18.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.clear_preference))
                Spacer(modifier = Modifier.padding(4.dp))
                SingleChoiceSegmentedButtonRow {
                    listOf(
                        "Daily",
                        "Weekly",
                        "Monthly",
                        "Never"
                    ).forEachIndexed { index, preference ->
                        SegmentedButton(
                            selected = currentClearPreference == preference,
                            onClick = {
                                coroutineScope.launch {
                                    taskListViewModel.cancelScheduleDeletion(currentClearPreference)
                                    GritDatastore.setClearPreferences(context, preference)
                                    taskListViewModel.scheduleDeletion(preference)
                                }
                            },
                            shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                3 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                else -> RectangleShape
                            }
                        ) {
                            Text(text = preference)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {

                // debug buttons
                if (context.packageName.contains(".debug")) {
                    Button(
                        onClick = {
                            val intent = Intent(context, NotificationReceiver::class.java).apply {
                                action = IntentActions.HABIT_NOTIFICATION.action
                                putExtra("1", "OK")
                                putExtra("2", "desc")
                                putExtra("3", 1L)
                            }

                            context.sendBroadcast(intent)
                        }
                    ) {
                        Text("Send Notification Intent")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, NotificationReceiver::class.java).apply {
                                action = IntentActions.TASKS_DELETION.action
                            }

                            context.sendBroadcast(intent)
                        }
                    ) {
                        Text("Send Deletion Intent")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, NotificationReceiver::class.java).apply {
                                action = IntentActions.ADD_HABIT_STATUS.action
                            }

                            context.sendBroadcast(intent)
                        }
                    ) {
                        Text("Send Status Intent")
                    }
                }

                // Credits
                Text(
                    text = "Made by shub39",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(modifier = Modifier.size(32.dp), onClick = {
                        openLinkInBrowser(context, "https://github.com/shub39/Grit")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.github_mark),
                            contentDescription = null,
                        )
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    IconButton(modifier = Modifier.size(32.dp), onClick = {
                        openLinkInBrowser(
                            context, "https://discord.gg/https://discord.gg/nxA2hgtEKf"
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.discord_svgrepo_com),
                            contentDescription = null,
                        )
                    }
                }
            }
        }


    }
}