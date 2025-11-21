import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.materialkolor.DynamicMaterialTheme
import com.shub39.grit.core.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
    val stateProvider = SyncedStateProvider()

    singleWindowApplication(
        state = WindowState(size = DpSize(1000.dp, 600.dp)),
        title = "Grit"
    ) {
        val windowSizeClass = calculateWindowSizeClass()

        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            var isDark by remember { mutableStateOf(true) }

            DynamicMaterialTheme(
                primary = Color(0xFABD2F),
                isDark = isDark
            ) {
                var showApp by remember { mutableStateOf(false) }

                if (showApp) {
                    App(
                        stateProvider = stateProvider,
                        isDark = isDark,
                        onSwitchTheme = { isDark = it }
                    )
                } else {
                    val isValidUrl by stateProvider.isValidUrl.collectAsState()

                    var url by remember { mutableStateOf("") }

                    Surface {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Grit Desktop",
                                style = MaterialTheme.typography.headlineLarge
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = url,
                                onValueChange = {
                                    url = it
                                    stateProvider.checkUrl(it)
                                },
                                label = { Text("Enter Server Url") },
                                placeholder = { Text("192.168.") },
                                shape = MaterialTheme.shapes.medium,
                                trailingIcon = {
                                    if (isValidUrl) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    stateProvider.setUrl(url)
                                    showApp = true
                                },
                                enabled = isValidUrl
                            ) {
                                Text("Start App")
                            }
                        }
                    }
                }
            }
        }
    }
}