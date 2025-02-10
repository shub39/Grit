package com.shub39.grit.core.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.BetterIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    onCategoryClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLookAndFeelClick: () -> Unit,
    onBackupClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                                text = stringResource(R.string.categories)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.edit_categories)
                            )
                        },
                        trailingContent = {
                            BetterIconButton (
                                onClick = onCategoryClick
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
                                text = stringResource(R.string.look_and_feel)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.look_and_feel_desc)
                            )
                        },
                        trailingContent = {
                            BetterIconButton (
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
                            BetterIconButton (
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
                            BetterIconButton (
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
}