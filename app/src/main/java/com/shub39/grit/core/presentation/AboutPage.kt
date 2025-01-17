package com.shub39.grit.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage() {
    val context = LocalContext.current

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
                        text = stringResource(R.string.about)
                    )
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
                        supportingContent = {
                            Column {
                                Text(text = context.packageName)
                                Text(text = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "")
                            }
                        },
                        trailingContent = {
                            Row {
                                BetterIconButton(
                                    onClick = { openLinkInBrowser(context, "https://github.com/shub39/Grit") }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.github_mark),
                                        modifier = Modifier.size(24.dp),
                                        contentDescription = "Github"
                                    )
                                }

                                BetterIconButton(
                                    onClick = { openLinkInBrowser(context, "https://discord.gg/https://discord.gg/nxA2hgtEKf") }
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
}