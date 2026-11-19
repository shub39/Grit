/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.shared.ui.ios

import androidx.compose.ui.window.ComposeUIViewController
import com.shub39.grit.logic.ios.GritSdk
import org.koin.plugin.module.dsl.startKoin
import platform.UIKit.UIViewController

private var koinStarted = false

/**
 * Hosts the whole Grit UI and starts the Koin graph.
 *
 * [GritSdk.configure] must have run first, because
 * [IosModules][com.shub39.grit.logic.ios.IosModules] reads the platform services the Swift app
 * hands over.
 */
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController {
    if (!koinStarted) {
        startKoin<GritIosKoin>()
        koinStarted = true
    }

    return ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) {
        GritIosApp()
    }
}
