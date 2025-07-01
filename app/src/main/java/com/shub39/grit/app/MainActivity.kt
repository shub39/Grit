package com.shub39.grit.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.grit.core.presentation.createNotificationChannel
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.util.Utils
import com.shub39.grit.viewmodels.MainViewModel
import com.shub39.grit.viewmodels.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : FragmentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        createNotificationChannel(this)
        enableEdgeToEdge()

        setContent {
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            val isAppUnlocked by remember { mutableStateOf(mainViewModel.isAppUnlocked()) }
            var showContent by remember { mutableStateOf(false) }

            LaunchedEffect(settingsState.biometric, isAppUnlocked) {
                when {
                    !settingsState.biometric -> {
                        showContent = true
                    }

                    isAppUnlocked -> {
                        showContent = true
                    }

                    else -> {
                        showBiometricPrompt(
                            onSuccess = {
                                mainViewModel.setAppUnlocked(true)
                                showContent = true
                            },
                            onError = { errorCode, errString ->
                                handleBiometricError(errString) {
                                    showContent = true
                                }
                            }
                        )
                    }
                }
            }

            if (showContent) {
                Grit(svm = settingsViewModel)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to access Grit")
            .setSubtitle("Use your biometric credential")
            .setAllowedAuthenticators(Utils.getAuthenticators())
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun handleBiometricError(
        errString: CharSequence,
        onComplete: () -> Unit
    ) {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(Utils.getAuthenticators()) != BiometricManager.BIOMETRIC_SUCCESS) {
            mainViewModel.setAppUnlocked(true)
            settingsViewModel.onAction(SettingsAction.ChangeBiometricLock(false))
            Toast.makeText(
                this,
                "Authentication error: $errString",
                Toast.LENGTH_SHORT
            ).show()
            onComplete()
        }
    }
}