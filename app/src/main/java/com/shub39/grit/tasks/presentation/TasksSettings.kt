package com.shub39.grit.tasks.presentation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.shub39.grit.R
import com.shub39.grit.core.data.GritDatastore
import com.shub39.grit.tasks.domain.ClearPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSettings(
    tvm: TaskListViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentClearPreference by GritDatastore.clearPreferences(context)
        .collectAsState(initial = ClearPreferences.NEVER.value)

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
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.clear_preference),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.clear_preference_exp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    var expanded by remember { mutableStateOf(false) }

                    Row {
                        Button(
                            onClick = { expanded = true }
                        ) {
                            Text(
                                text = currentClearPreference,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            ClearPreferences.entries.forEachIndexed { index, preference ->
                                DropdownMenuItem(
                                    text = { Text(preference.value) },
                                    onClick = {
                                        coroutineScope.launch {
                                            tvm.cancelScheduleDeletion(preference.value)
                                            GritDatastore.setClearPreferences(context, preference.value)
                                            tvm.scheduleDeletion(preference.value)
                                        }

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