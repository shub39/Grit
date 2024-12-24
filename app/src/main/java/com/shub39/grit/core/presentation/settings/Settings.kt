package com.shub39.grit.core.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.shub39.grit.R
import com.shub39.grit.core.presentation.GritTheme
import com.shub39.grit.tasks.domain.Category
import com.shub39.grit.tasks.domain.ClearPreferences

// for some reason the ListItem and TopAppBar composables wont style
// the text according to their defaults, need to investigate this
// manually styling for now
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    var deleteCategory: Category? by remember { mutableStateOf(null) }

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
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.clear_preference),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.clear_preference_exp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                },
                trailingContent = {
                    var expanded by remember { mutableStateOf(false) }

                    Row {
                        Button(
                            onClick = { expanded = true }
                        ) {
                            Text(
                                text = state.currentClearPreference,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            ClearPreferences.entries.forEach { preference ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = preference.value,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        action(SettingsAction.UpdateClearPreference(preference.value))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.categories),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.edit_categories),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.categories, key = { it.id }) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    deleteCategory = it
                                },
                                enabled = state.categories.size > 1
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    if (deleteCategory != null) {
        BasicAlertDialog(
            onDismissRequest = { deleteCategory = null }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_warning_24),
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(R.string.delete_category),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            action(SettingsAction.DeleteCategory(deleteCategory!!))
                            deleteCategory = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    device = "spec:width=673dp,height=841dp", showSystemUi = true, showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun TaskSettingsPreview() {
    GritTheme {
        Settings(
            state = SettingsState(),
            action = {}
        )
    }
}