import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.singleWindowApplication
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.grit.shared.ui.LocalWindowSizeClass
import com.shub39.grit.shared.ui.app.MainApp
import com.shub39.grit.shared.ui.theme.GritTheme
import com.shub39.grit.shared.ui.viewmodel.MainViewModel
import com.shub39.grit.web_demo.di.AppModule
import org.koin.compose.viewmodel.koinViewModel
import org.koin.plugin.module.dsl.startKoin

fun main() {
    startKoin<AppModule>()

    singleWindowApplication(
        title = "Grit"
    ) {
        val windowSizeClass = calculateWindowSizeClass()
        val viewmodel = koinViewModel<MainViewModel>()
        val state by viewmodel.state.collectAsStateWithLifecycle()

        CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
            GritTheme(state.theme) {
                MainApp(
                    state = state,
                    onNavigateToPaywall = { }
                )
            }
        }
    }
}