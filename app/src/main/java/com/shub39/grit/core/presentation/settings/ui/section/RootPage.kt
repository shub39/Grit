package com.shub39.grit.core.presentation.settings.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.presentation.getRandomLine
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.component.AboutApp
import com.shub39.grit.core.presentation.settings.ui.component.detachedItemShape
import com.shub39.grit.core.presentation.settings.ui.component.endItemShape
import com.shub39.grit.core.presentation.settings.ui.component.leadingItemShape
import com.shub39.grit.core.presentation.settings.ui.component.listItemColors
import com.shub39.grit.core.presentation.settings.ui.component.middleItemShape
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.backup
import grit.shared.core.generated.resources.backup_desc
import grit.shared.core.generated.resources.biometric_lock
import grit.shared.core.generated.resources.biometric_lock_desc
import grit.shared.core.generated.resources.check_list
import grit.shared.core.generated.resources.download
import grit.shared.core.generated.resources.grit_plus
import grit.shared.core.generated.resources.look_and_feel
import grit.shared.core.generated.resources.look_and_feel_desc
import grit.shared.core.generated.resources.palette
import grit.shared.core.generated.resources.pause_notifications
import grit.shared.core.generated.resources.pause_notifications_desc
import grit.shared.core.generated.resources.reorder_tasks
import grit.shared.core.generated.resources.reorder_tasks_desc
import grit.shared.core.generated.resources.settings
import grit.shared.core.generated.resources.show_habits
import grit.shared.core.generated.resources.show_habits_desc
import grit.shared.core.generated.resources.staring_day
import grit.shared.core.generated.resources.use_24Hr
import grit.shared.core.generated.resources.use_24Hr_desc
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RootPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateToChangelog: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onAction(SettingsAction.OnCheckBiometric(context))
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        LargeFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Text(text = stringResource(Res.string.settings))
            },
            subtitle = {
                Text(text = getRandomLine())
            },
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 60.dp)
        ) {
            item { AboutApp() }

            item {
                Card(
                    onClick = onNavigateToPaywall,
                    modifier = Modifier.padding(top = 16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add),
                            contentDescription = "Grit Plus",
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(Res.string.grit_plus),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_forward),
                            contentDescription = "Grit Plus"
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.pause_notifications)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.pause_notifications_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.pauseNotifications,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangePauseNotifications(it))
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(leadingItemShape())
                    )

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.reorder_tasks)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.reorder_tasks_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.reorderTasks,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangeReorderTasks(it))
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape())
                    )

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.show_habits)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.show_habits_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.startingPage == Sections.Habits,
                                onCheckedChange = {
                                    onAction(
                                        SettingsAction.ChangeStartingPage(
                                            if (it) Sections.Habits else Sections.Tasks
                                        )
                                    )
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape())
                    )

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.staring_day)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.startOfTheWeek == DayOfWeek.SUNDAY,
                                onCheckedChange = {
                                    onAction(
                                        SettingsAction.ChangeStartOfTheWeek(if (it) DayOfWeek.SUNDAY else DayOfWeek.MONDAY)
                                    )
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape())
                    )

                    if (state.isBiometricLockAvailable) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.biometric_lock)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.biometric_lock_desc)
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.isBiometricLockOn == true,
                                    onCheckedChange = {
                                        onAction(SettingsAction.ChangeBiometricLock(it))
                                    }
                                )
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape())
                        )
                    }

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.use_24Hr)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.use_24Hr_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.is24Hr,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangeIs24Hr(it))
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(endItemShape())
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ListItem(
                        modifier = Modifier
                            .clip(leadingItemShape())
                            .clickable { onNavigateToLookAndFeel() },
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.look_and_feel)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.look_and_feel_desc)
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate"
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.palette),
                                contentDescription = "Navigate",
                            )
                        },
                        colors = listItemColors()
                    )

                    ListItem(
                        modifier = Modifier
                            .clip(endItemShape())
                            .clickable { onNavigateToBackup() },
                        colors = listItemColors(),
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.backup)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.backup_desc)
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate"
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.download),
                                contentDescription = "Backup",
                            )
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                ListItem(
                    colors = listItemColors(),
                    leadingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.check_list),
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_forward),
                            contentDescription = "Navigate"
                        )
                    },
                    headlineContent = {
                        Text(
                            text = "Changelog"
                        )
                    },
                    modifier = Modifier
                        .clip(detachedItemShape())
                        .clickable { onNavigateToChangelog() }
                )
            }
        }
    }
}