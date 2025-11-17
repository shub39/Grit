import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.grit.core.utils.LocalWindowSizeClass
import di.initKoin

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
    initKoin()

    singleWindowApplication(
        state = WindowState(size = DpSize(1000.dp, 600.dp)),
        title = "Grit"
    ) {
        val windowSizeClass = calculateWindowSizeClass()

        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            App()
        }
    }
}