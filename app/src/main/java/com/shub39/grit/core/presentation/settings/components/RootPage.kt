package com.shub39.grit.core.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.BuildConfig
import com.shub39.grit.R
import com.shub39.grit.core.domain.Pages
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.settings.SettingsState
import com.shub39.grit.util.Utils
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Discord
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Coffee
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
    val uriHandler = LocalUriHandler.current

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
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    ListItem(
                        modifier = Modifier.clip(MaterialTheme.shapes.medium),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            trailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            supportingColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                            )
                        },
                        trailingContent = {
                            Row {
                                FilledIconButton(
                                    onClick = {
                                        uriHandler.openUri("https://discord.gg/https://discord.gg/nxA2hgtEKf")
                                    }
                                ) {
                                    Icon(
                                        imageVector = FontAwesomeIcons.Brands.Discord,
                                        contentDescription = "Discord",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                FilledIconButton(
                                    onClick = {
                                        uriHandler.openUri("https://github.com/shub39/Grit")
                                    }
                                ) {
                                    Icon(
                                        imageVector = FontAwesomeIcons.Brands.Github,
                                        contentDescription = "Github",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )

                    Button(
                        onClick = {
                            uriHandler.openUri("https://buymeacoffee.com/shub39")
                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Coffee,
                                contentDescription = "Coffee",
                                modifier = Modifier.size(20.dp)
                            )

                            Text(text = stringResource(R.string.sponsor))
                        }
                    }
                }
            }

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

            if (Utils.authenticaticationAvailable(context)) {
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
                        FilledTonalIconButton(
                            onClick = onNavigateToLookAndFeel
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
                        FilledTonalIconButton(
                            onClick = onNavigateToBackup
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
                            text = stringResource(R.string.about_libraries)
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = onNavigateToAboutLibraries
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
                Spacer(modifier = Modifier.padding(60.dp))
            }
        }
    }
}