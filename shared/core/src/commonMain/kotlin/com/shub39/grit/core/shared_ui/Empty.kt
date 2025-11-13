package com.shub39.grit.core.shared_ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import org.jetbrains.compose.resources.stringResource

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .padding(top = 150.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = LocalContentColor.current.copy(alpha = 0.4f)

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = color
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = stringResource(Res.string.add),
            textAlign = TextAlign.Center,
            color = color
        )
    }
}