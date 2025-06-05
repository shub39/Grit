package com.shub39.grit.core.presentation.settings

import kotlinx.serialization.Serializable

sealed interface SettingsRoutes {
    @Serializable
    data object Root: SettingsRoutes

    @Serializable
    data object LookAndFeel: SettingsRoutes

    @Serializable
    data object Backup: SettingsRoutes

    @Serializable
    data object AboutLibraries: SettingsRoutes
}