package com.shub39.grit.core.presentation.settings.ui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.domain.Changelog
import com.shub39.grit.core.domain.VersionEntry
import com.shub39.grit.core.theme.GritTheme
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.arrow_back
import grit.shared.core.generated.resources.arrow_forward
import grit.shared.core.generated.resources.changelog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Changelog(
    modifier: Modifier = Modifier,
    changelog: Changelog,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.changelog)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 60.dp,
                start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp
            ),
        ) {
            changelog.forEach { versionEntry ->
                item {
                    Text(
                        text = versionEntry.version,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                items(versionEntry.changes) { change ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_forward),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )

                        Text(
                            text = change,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChangelogPreview() {
    GritTheme {
        Changelog(
            changelog = listOf(
                VersionEntry(
                    version = "1.0.0",
                    changes = listOf("Initial release", "Added new feature")
                ),
                VersionEntry(
                    version = "1.1.0",
                    changes = listOf("Bug fixes", "Improved performance")
                )
            ),
            onNavigateBack = {}
        )
    }
}