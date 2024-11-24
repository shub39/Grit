package com.shub39.grit.core.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openLinkInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}