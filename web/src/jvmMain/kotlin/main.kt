import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        state = WindowState(size = DpSize(1000.dp, 600.dp)),
        title = "Grit"
    ) {
        App()
    }
}