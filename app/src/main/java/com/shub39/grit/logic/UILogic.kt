package com.shub39.grit.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object UILogic {
    @Composable
    fun getCardColors(priority: Boolean): CardColors {
        return when (priority) {
            true -> CardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                disabledContentColor = Color.Red,
                disabledContainerColor = Color.Red
            )

            else -> CardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContentColor = Color.Yellow,
                disabledContainerColor = Color.Yellow
            )
        }
    }

    fun openLinkInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}