import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.grit.core.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
    singleWindowApplication(
        title = "Hot Reload"
    ) {
        val windowSizeClass = calculateWindowSizeClass()

        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            App()
        }
    }
}