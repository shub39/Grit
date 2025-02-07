package com.shub39.grit.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R

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
            painter = painterResource(R.drawable.baseline_format_list_bulleted_add_24),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = color
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = stringResource(id = R.string.add),
            textAlign = TextAlign.Center,
            color = color
        )
    }
}