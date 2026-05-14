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
package com.shub39.grit.core.presentation.settings.ui.section

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.GritPreviewWrapper
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.component.AboutApp
import com.shub39.grit.core.presentation.settings.ui.component.LocalePickerSheet
import com.shub39.grit.core.shared_ui.detachedItemShape
import com.shub39.grit.core.shared_ui.endItemShape
import com.shub39.grit.core.shared_ui.leadingItemShape
import com.shub39.grit.core.shared_ui.listItemColors
import com.shub39.grit.core.shared_ui.middleItemShape
import com.shub39.grit.core.theme.flexFontEmphasis
import com.shub39.grit.core.theme.flexFontRounded
import com.shub39.grit.warning.WarningManager
import com.shub39.grit.warning.WarningReminder
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.backup
import grit.shared.core.generated.resources.backup_desc
import grit.shared.core.generated.resources.biometric_lock
import grit.shared.core.generated.resources.biometric_lock_desc
import grit.shared.core.generated.resources.changelog
import grit.shared.core.generated.resources.check_list
import grit.shared.core.generated.resources.download
import grit.shared.core.generated.resources.grit_plus
import grit.shared.core.generated.resources.language
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
import org.jetbrains.compose.resources.painterResource
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
) {
    var showLocalePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) { onAction(SettingsAction.OnCheckBiometric(context)) }

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
            if (WarningManager.showWarning()) {
                item { WarningReminder() }
            }

            // about app, info, build version, links
            item { AboutApp() }

            // Grit Plus
            item {
                Card(
                    onClick = onNavigateToPaywall,
                    shape = RoundedCornerShape(1000.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.grit_plus),
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = flexFontRounded()
                                ),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_forward),
                            contentDescription = "Grit Plus",
                        )
                    }
                }
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
                            Switch(
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
                            Switch(
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
                            Switch(
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
                            Switch(
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
                                Switch(
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
                            Switch(
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

            // language picker
            if (Build.VERSION.SDK_INT >= 33) {
                item {
                    ListItem(
                        colors = listItemColors(),
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.language),
                                contentDescription = null,
                            )
                        },
                        headlineContent = { Text(text = stringResource(Res.string.language)) },
                        supportingContent = {
                            Text(text = LocalLocale.current.platformLocale.displayLanguage)
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_forward),
                                contentDescription = "Navigate",
                            )
                        },
                        modifier =
                            Modifier.clip(detachedItemShape()).clickable { showLocalePicker = true },
                    )
                }
            }

            // Changelogs
            item {
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
                        Modifier.clip(detachedItemShape()).clickable { onNavigateToChangelog() },
                )
            }
        }

        if (showLocalePicker) {
            LocalePickerSheet(onDismissRequest = { showLocalePicker = false })
        }
    }
}

@PreviewWrapper(GritPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    RootPage(
        state = SettingsState(),
        onAction = {},
        onNavigateToLookAndFeel = {},
        onNavigateToBackup = {},
        onNavigateToPaywall = {},
        onNavigateToChangelog = {},
    )
}
