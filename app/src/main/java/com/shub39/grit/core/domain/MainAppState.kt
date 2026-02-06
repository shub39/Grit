package com.shub39.grit.core.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.core.theme.Theme

@Stable
@Immutable
data class MainAppState(
    val isAppUnlocked: Boolean = false,
    val isBiometricLockOn: Boolean? = null,
    val isUserSubscribed: Boolean = false,
    val startingSection: Sections = Sections.Tasks,
    val theme: Theme = Theme()
)