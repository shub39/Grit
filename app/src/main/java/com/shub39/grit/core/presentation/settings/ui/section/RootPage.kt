package com.shub39.grit.core.presentation.settings.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.presentation.component.PageFill
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.core.presentation.settings.ui.component.AboutApp
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        LargeFlexibleTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Text(text = stringResource(R.string.settings))
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    onClick = { onAction(SettingsAction.OnPaywallShow) },
                    modifier = Modifier.padding(16.dp),
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
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Grit Plus",
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.grit_plus),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Grit Plus"
                        )
                    }
                }
            }

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
                                checked = state.biometric == true,
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
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = "Navigate"
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = "Navigate",
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
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = "Navigate"
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = "Backup",
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
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.about_libraries)
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = "Navigate"
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "About Libraries"
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