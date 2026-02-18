package com.shub39.grit.core.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.grit.core.theme.Theme
import kotlinx.serialization.Serializable

@Serializable
data class VersionEntry(
    val version: String,
    val changes: List<String>
)

typealias Changelog = List<VersionEntry>

@Stable
@Immutable
data class MainAppState(
    val isAppUnlocked: Boolean = false,
    val isBiometricLockOn: Boolean? = null,
    val isUserSubscribed: Boolean = false,
    val startingSection: Sections = Sections.Tasks,
    val theme: Theme = Theme(),
    val currentChangelog: VersionEntry? = null
)