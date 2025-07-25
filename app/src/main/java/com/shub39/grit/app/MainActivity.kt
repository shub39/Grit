package com.shub39.grit.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.grit.R
import com.shub39.grit.core.presentation.components.InitialLoading
import com.shub39.grit.core.presentation.createNotificationChannel
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.core.presentation.theme.GritTheme
import com.shub39.grit.util.Utils
import com.shub39.grit.viewmodels.MainViewModel
import com.shub39.grit.viewmodels.SettingsViewModel
import com.shub39.grit.widgets.TodoListWidgetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : FragmentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val todoWidgetRepo: TodoListWidgetRepository by lazy { get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        createNotificationChannel(this)
        enableEdgeToEdge()
        
        // Update widgets when app opens
        CoroutineScope(Dispatchers.IO).launch {
            todoWidgetRepo.update()
        }

        setContent {
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            val isAppUnlocked by mainViewModel.isAppUnlocked.collectAsStateWithLifecycle()
            var showContent by remember { mutableStateOf(false) }

            LaunchedEffect(settingsState.biometric, isAppUnlocked) {
                settingsState.biometric?.let {
                    when {
                        !it -> {
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
                                    handleBiometricError(errorCode, errString) {
                                        showContent = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            GritTheme(
                theme = settingsState.theme
            ) {
                if (showContent) {
                    Grit(svm = settingsViewModel)
                } else {
                    InitialLoading()
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
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_lock))
            .setAllowedAuthenticators(Utils.getAuthenticators())
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun handleBiometricError(
        errorCode: Int,
        errString: CharSequence,
        onComplete: () -> Unit
    ) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
            BiometricPrompt.ERROR_CANCELED -> {
                Toast.makeText(this, getString(R.string.biometric_failed), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }

            BiometricPrompt.ERROR_NO_BIOMETRICS,
            BiometricPrompt.ERROR_HW_NOT_PRESENT,
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                mainViewModel.setAppUnlocked(true)
                settingsViewModel.onAction(SettingsAction.ChangeBiometricLock(false))
                Toast.makeText(
                    this,
                    getString(R.string.biometric_not_available),
                    Toast.LENGTH_LONG
                ).show()
                onComplete()
            }

            else -> {
                Toast.makeText(this, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Update widgets when app resumes
        CoroutineScope(Dispatchers.IO).launch {
            todoWidgetRepo.update()
        }
    }
}