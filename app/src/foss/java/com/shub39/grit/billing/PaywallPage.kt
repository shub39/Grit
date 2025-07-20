package com.shub39.grit.billing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.core.domain.AppTheme
import com.shub39.grit.core.domain.Fonts
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.core.presentation.theme.Theme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Coffee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallPage(
    isPlusUser: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    BackHandler { onDismissRequest() }

    Scaffold { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.foss),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = stringResource(R.string.foss_desc),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = { uriHandler.openUri("https://buymeacoffee.com/shub39") }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Coffee,
                            contentDescription = "Buy me a coffee",
                            modifier = Modifier.size(24.dp)
                        )

                        Text(text = stringResource(R.string.bmc))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    GritTheme(
        theme = Theme(
            seedColor = Color.Red,
            appTheme = AppTheme.DARK,
            font = Fonts.FIGTREE
        )
    ) {
        PaywallPage(
            isPlusUser = false,
            onDismissRequest = {}
        )
    }
}