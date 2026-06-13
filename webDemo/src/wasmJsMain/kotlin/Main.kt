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
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.app.MainApp
import com.shub39.grit.shared.ui.theme.GritTheme
import com.shub39.grit.shared.ui.viewmodel.MainViewModel
import com.shub39.grit.web_demo.di.AppModule
import org.koin.compose.viewmodel.koinViewModel
import org.koin.plugin.module.dsl.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin<AppModule>()

    ComposeViewport {
        val windowSizeClass = calculateWindowSizeClass()
        val viewmodel = koinViewModel<MainViewModel>()
        val state by viewmodel.state.collectAsStateWithLifecycle()

        CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
            GritTheme(state.theme) { MainApp(state = state, onNavigateToPaywall = {}) }
        }
    }
}
