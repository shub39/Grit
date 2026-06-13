/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.shared.ui.setting.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.settings.Sections
import com.shub39.grit.shared.ui.GritPreviewWrapper
import com.shub39.grit.shared.ui.components.ExpressiveSwitch
import com.shub39.grit.shared.ui.components.detachedItemShape
import com.shub39.grit.shared.ui.components.endItemShape
import com.shub39.grit.shared.ui.components.leadingItemShape
import com.shub39.grit.shared.ui.components.listItemColors
import com.shub39.grit.shared.ui.components.middleItemShape
import com.shub39.grit.shared.ui.setting.SettingsAction
import com.shub39.grit.shared.ui.setting.SettingsState
import com.shub39.grit.shared.ui.setting.ui.component.LocalePickerSheet
import com.shub39.grit.shared.ui.theme.flexFontEmphasis
import grit.shared.ui.generated.resources.*
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** Root settings page all roads start from here */
@Composable
fun RootPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateToChangelog: () -> Unit,
    onNavigateToAppInfo: () -> Unit,
) {
    var showLocalePicker by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).fillMaxSize()) {
        LargeFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Text(text = stringResource(Res.string.settings), fontFamily = flexFontEmphasis())
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Grit Plus
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(Res.string.grit_plus)) },
                    colors = listItemColors(),
                    modifier =
                        Modifier.clip(detachedItemShape()).clickable { onNavigateToPaywall() },
                    trailingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_forward),
                            contentDescription = "Grit Plus",
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.grit_icon),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }

            // General settings
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(Res.string.pause_notifications))
                        },
                        supportingContent = {
                            Text(text = stringResource(Res.string.pause_notifications_desc))
                        },
                        trailingContent = {
                            ExpressiveSwitch(
                                checked = state.pauseNotifications,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangePauseNotifications(it))
                                },
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(leadingItemShape()),
                    )

                    ListItem(
                        headlineContent = { Text(text = stringResource(Res.string.reorder_tasks)) },
                        supportingContent = {
                            Text(text = stringResource(Res.string.reorder_tasks_desc))
                        },
                        trailingContent = {
                            ExpressiveSwitch(
                                checked = state.reorderTasks,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangeReorderTasks(it))
                                },
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape()),
                    )

                    ListItem(
                        headlineContent = { Text(text = stringResource(Res.string.show_habits)) },
                        supportingContent = {
                            Text(text = stringResource(Res.string.show_habits_desc))
                        },
                        trailingContent = {
                            ExpressiveSwitch(
                                checked = state.startingPage == Sections.Habits,
                                onCheckedChange = {
                                    onAction(
                                        SettingsAction.ChangeStartingPage(
                                            if (it) Sections.Habits else Sections.Tasks
                                        )
                                    )
                                },
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape()),
                    )

                    ListItem(
                        headlineContent = { Text(text = stringResource(Res.string.staring_day)) },
                        trailingContent = {
                            ExpressiveSwitch(
                                checked = state.startOfTheWeek == DayOfWeek.SUNDAY,
                                onCheckedChange = {
                                    onAction(
                                        SettingsAction.ChangeStartOfTheWeek(
                                            if (it) DayOfWeek.SUNDAY else DayOfWeek.MONDAY
                                        )
                                    )
                                },
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape()),
                    )

                    if (state.isBiometricLockAvailable) {
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(Res.string.biometric_lock))
                            },
                            supportingContent = {
                                Text(text = stringResource(Res.string.biometric_lock_desc))
                            },
                            trailingContent = {
                                ExpressiveSwitch(
                                    checked = state.isBiometricLockOn == true,
                                    onCheckedChange = {
                                        onAction(SettingsAction.ChangeBiometricLock(it))
                                    },
                                )
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape()),
                        )
                    }

                    ListItem(
                        headlineContent = { Text(text = stringResource(Res.string.use_24Hr)) },
                        supportingContent = {
                            Text(text = stringResource(Res.string.use_24Hr_desc))
                        },
                        trailingContent = {
                            ExpressiveSwitch(
                                checked = state.is24Hr,
                                onCheckedChange = { onAction(SettingsAction.ChangeIs24Hr(it)) },
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(endItemShape()),
                    )
                }
            }

            // look and feel customizations
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        modifier =
                            Modifier.clip(leadingItemShape()).clickable {
                                onNavigateToLookAndFeel()
                            },
                        headlineContent = { Text(text = stringResource(Res.string.look_and_feel)) },
                        supportingContent = {
                            Text(text = stringResource(Res.string.look_and_feel_desc))
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate",
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.palette),
                                contentDescription = "Navigate",
                            )
                        },
                        colors = listItemColors(),
                    )

                    ListItem(
                        modifier = Modifier.clip(endItemShape()).clickable { onNavigateToBackup() },
                        colors = listItemColors(),
                        headlineContent = { Text(text = stringResource(Res.string.backup)) },
                        supportingContent = { Text(text = stringResource(Res.string.backup_desc)) },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate",
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.download),
                                contentDescription = "Backup",
                            )
                        },
                    )
                }
            }

            // Changelogs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        colors = listItemColors(),
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.info),
                                contentDescription = null,
                            )
                        },
                        supportingContent = {
                            Text(text = "Grit ${state.currentVersion ?: "x.x.x"}")
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate",
                            )
                        },
                        headlineContent = { Text(text = stringResource(Res.string.about)) },
                        modifier =
                            Modifier.clip(leadingItemShape()).clickable { onNavigateToAppInfo() },
                    )

                    ListItem(
                        colors = listItemColors(),
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.check_list),
                                contentDescription = null,
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate",
                            )
                        },
                        headlineContent = { Text(text = stringResource(Res.string.changelog)) },
                        modifier =
                            Modifier.clip(endItemShape()).clickable { onNavigateToChangelog() },
                    )
                }
            }

            // language picker
            languagePicker(onClick = { showLocalePicker = true })
        }

        if (showLocalePicker) {
            LocalePickerSheet(onDismissRequest = { showLocalePicker = false })
        }
    }
}

expect fun LazyListScope.languagePicker(onClick: () -> Unit)

@PreviewWrapper(GritPreviewWrapper::class)
@PreviewLightDark
@Composable
private fun Preview() {
    RootPage(
        state = SettingsState(),
        onAction = {},
        onNavigateToLookAndFeel = {},
        onNavigateToBackup = {},
        onNavigateToPaywall = {},
        onNavigateToChangelog = {},
        onNavigateToAppInfo = {},
    )
}
