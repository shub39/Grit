package com.shub39.grit.core.presentation.settings.components

import android.content.ClipData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.PageFill
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Bitcoin
import compose.icons.fontawesomeicons.brands.Discord
import compose.icons.fontawesomeicons.brands.Ethereum
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Coffee
import compose.icons.fontawesomeicons.solid.Copy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit
) = PageFill {
    val coroutineScope = rememberCoroutineScope()

    val clipboard = LocalClipboard.current
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
                FilledTonalButton(
                    onClick = {
                        uriHandler.openUri("https://buymeacoffee.com/shub39")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Coffee,
                        contentDescription = "Github",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.sponsor)
                    )
                }
            }

            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = FontAwesomeIcons.Brands.Bitcoin,
                            contentDescription = "Bitcoin",
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    overlineContent = {
                        Text("BTC")
                    },
                    headlineContent = {
                        Text(
                            text = "bc1qg7k4d9pm6hddns9dq30vh0u8e90cxehy4eydsa"
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText("address", "bc1qg7k4d9pm6hddns9dq30vh0u8e90cxehy4eydsa")
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Copy,
                                contentDescription = "Copy",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = FontAwesomeIcons.Brands.Ethereum,
                            contentDescription = "Ethereum",
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    overlineContent = {
                        Text("ETH")
                    },
                    headlineContent = {
                        Text(
                            text = "0xd1b6A1Be6416fb153f78e3960208d1f8D97af132"
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText("address", "0xd1b6A1Be6416fb153f78e3960208d1f8D97af132")
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Copy,
                                contentDescription = "Copy",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}