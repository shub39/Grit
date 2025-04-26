package com.shub39.grit.core.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.PageFill
import com.shub39.grit.core.presentation.getRandomLine
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Discord
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Coffee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit
) = PageFill {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

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
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = "App Icon",
                            modifier = Modifier.size(150.dp)
                        )

                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = context.packageName,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    uriHandler.openUri("https://github.com/shub39/Grit")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Github,
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = "Github"
                                )
                            }

                            IconButton(
                                onClick = {
                                    uriHandler.openUri("https://discord.gg/https://discord.gg/nxA2hgtEKf")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Discord,
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = "Discord"
                                )
                            }
                        }
                    }
                }
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Made By shub39")
                    },
                    trailingContent = {
                        Row {
                            FilledTonalIconButton(
                                onClick = {
                                    uriHandler.openUri("https://github.com/shub39")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Github,
                                    contentDescription = "Github",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            FilledTonalIconButton(
                                onClick = {
                                    uriHandler.openUri("https://buymeacoffee.com/shub39")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Coffee,
                                    contentDescription = "Github",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
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