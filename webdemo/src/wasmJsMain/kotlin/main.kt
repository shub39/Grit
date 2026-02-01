import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.shub39.grit.core.utils.LocalWindowSizeClass

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
    ComposeViewport {
        val windowSizeClass = calculateWindowSizeClass()

        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            App()
        }
    }
}