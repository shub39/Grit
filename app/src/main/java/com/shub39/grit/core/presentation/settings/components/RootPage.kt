package com.shub39.grit.core.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.InfoCircle
import compose.icons.fontawesomeicons.solid.Palette
import compose.icons.fontawesomeicons.solid.Upload
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RootPage(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToAboutLibraries: () -> Unit
) = PageFill {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onAction(SettingsAction.OnCheckBiometric(context))
    }

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.settings))
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { AboutApp() }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.pause_notifications)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.pause_notifications_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.pauseNotifications,
                            onCheckedChange = {
                                onAction(SettingsAction.ChangePauseNotifications(it))
                            }
                        )
                    }
                )
            }

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
                            checked = state.startingPage == Pages.Habits,
                            onCheckedChange = {
                                onAction(
                                    SettingsAction.ChangeStartingPage(
                                        if (it) Pages.Habits else Pages.Tasks
                                    )
                                )
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
                            checked = state.startOfTheWeek == DayOfWeek.SUNDAY,
                            onCheckedChange = {
                                onAction(
                                    SettingsAction.ChangeStartOfTheWeek(if (it) DayOfWeek.SUNDAY else DayOfWeek.MONDAY)
                                )
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
                            checked = state.is24Hr,
                            onCheckedChange = {
                                onAction(SettingsAction.ChangeIs24Hr(it))
                            }
                        )
                    }
                )
            }

            if (state.biometricAvailable) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.biometric_lock)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.biometric_lock_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.biometric,
                                onCheckedChange = {
                                    onAction(SettingsAction.ChangeBiometricLock(it))
                                }
                            )
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                ListItem(
                    modifier = Modifier.clickable { onNavigateToLookAndFeel() },
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
                    leadingContent = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Palette,
                            contentDescription = "Navigate",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }

            item {
                ListItem(
                    modifier = Modifier.clickable { onNavigateToBackup() },
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
                    leadingContent = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Upload,
                            contentDescription = "Backup",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }

            item {
                ListItem(
                    modifier = Modifier.clickable { onNavigateToAboutLibraries() },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.about_libraries)
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.InfoCircle,
                            contentDescription = "About Libraries",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.padding(60.dp))
            }
        }
    }
}