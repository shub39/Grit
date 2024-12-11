package com.shub39.grit.tasks.presentation.tasks_settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.shub39.grit.R
import com.shub39.grit.core.presentation.GritTheme
import com.shub39.grit.tasks.domain.ClearPreferences

// for some reason the ListItem and TopAppBar composables wont style
// the text according to their defaults, need to investigate this
// manually styling for now
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSettings(
    state: TasksSettingsState,
    action: (TasksSettingsAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
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
                                        action(TasksSettingsAction.UpdateClearPreference(preference.value))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
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
        TaskSettings(
            state = TasksSettingsState(),
            action = {}
        )
    }
}