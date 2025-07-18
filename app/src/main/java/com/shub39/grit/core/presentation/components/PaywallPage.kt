package com.shub39.grit.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallPage(
    isPlusUser: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paywallOptions = remember {
        PaywallOptions.Builder(dismissRequest = onDismissRequest)
            .setShouldDisplayDismissButton(true)
            .build()
    }

    Box(modifier = modifier) {
        if (!isPlusUser) {
            Paywall(paywallOptions)
        } else {
            CustomerCenter(onDismiss = onDismissRequest)
        }
    }
}