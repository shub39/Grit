import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeViewport
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.utils.LocalWindowSizeClass

// Dummy ui for web, hosted in github pages, imma make it to a landing page or something
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
    val stateProvider = DummyStateProvider()

    ComposeViewport {
        val windowSizeClass = calculateWindowSizeClass()
        var isDark by remember { mutableStateOf(true) }

        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            DynamicMaterialTheme(
                primary = Color(0xFABD2F),
                isDark = isDark
            ) {
                App(
                    stateProvider = stateProvider,
                    isDark = isDark,
                    onSwitchTheme = { isDark = it }
                )
            }
        }
    }
}