package com.shub39.grit.core.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.getRandomLine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit
) = PageFill {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.about)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(300.dp),
                    contentDescription = null
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.app_name))
                    },
                    trailingContent = {
                        Row {
                            FilledIconButton(
                                onClick = {
                                    uriHandler.openUri("https://github.com/shub39/Grit")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.github_mark),
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = "Github"
                                )
                            }

                            FilledIconButton(
                                onClick = {
                                    uriHandler.openUri("https://discord.gg/https://discord.gg/nxA2hgtEKf")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.discord_svgrepo_com),
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = "Discord"
                                )
                            }
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Made By shub39")
                    },
                    supportingContent = {
                        Text(
                            text = getRandomLine(),
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Thin
                        )
                    }
                )
            }
        }
    }
}