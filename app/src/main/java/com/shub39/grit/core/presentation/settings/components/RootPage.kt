package com.shub39.grit.core.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.presentation.components.BetterIconButton
import com.shub39.grit.core.presentation.components.PageFill
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootPage(
    onAboutClick: () -> Unit,
    onLookAndFeelClick: () -> Unit,
    onBackupClick: () -> Unit,
    datastore: GritDatastore = koinInject()
) = PageFill {
    val coroutineScope = rememberCoroutineScope()

    val startingPage by datastore.getStartingPagePref().collectAsState(Pages.Tasks)
    val is24Hr by datastore.getIs24Hr().collectAsState(false)
    val startingDay by datastore.getStartOfTheWeekPref().collectAsState(DayOfWeek.MONDAY)

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.settings)
                )
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.show_habits)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.show_habits_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = startingPage == Pages.Habits,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    if (it) {
                                        datastore.setStartingPage(Pages.Habits)
                                    } else {
                                        datastore.setStartingPage(Pages.Tasks)
                                    }
                                }
                            }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.staring_day)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = startingDay == DayOfWeek.SUNDAY,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    if (it) {
                                        datastore.setStartOfWeek(DayOfWeek.SUNDAY)
                                    } else {
                                        datastore.setStartOfWeek(DayOfWeek.MONDAY)
                                    }
                                }
                            }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_24Hr)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.use_24Hr_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = is24Hr,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    datastore.setIs24Hr(it)
                                }
                            }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.look_and_feel)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.look_and_feel_desc)
                        )
                    },
                    trailingContent = {
                        BetterIconButton(
                            onClick = onLookAndFeelClick
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.backup)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.backup_desc)
                        )
                    },
                    trailingContent = {
                        BetterIconButton(
                            onClick = onBackupClick
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.about)
                        )
                    },
                    trailingContent = {
                        BetterIconButton(
                            onClick = onAboutClick
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}