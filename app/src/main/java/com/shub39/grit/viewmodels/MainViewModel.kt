package com.shub39.grit.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.billing.SubscriptionResult
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.MainAppState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val datastore: GritDatastore,
    private val billingHandler: BillingHandler
) : ViewModel() {
    var observerJob: Job? = null

    private val _state = MutableStateFlow(MainAppState())

    val state = _state.asStateFlow()
        .onStart {
            checkSubscription()
            observeDatastore()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainAppState()
        )

    fun setAppUnlocked(value: Boolean) {
        _state.update {
            it.copy(isAppUnlocked = value)
        }
    }
    fun setBiometricLock(value: Boolean) {
        viewModelScope.launch {
            datastore.setBiometricPref(value)
        }
    }
    fun updateSubscription() {
        viewModelScope.launch {
            checkSubscription()
        }
    }

    private fun observeDatastore() {
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
            combine(
                datastore.getPaletteStyle(),
                datastore.getSeedColorFlow(),
                datastore.getFontPrefFlow(),
                datastore.getMaterialYouFlow(),
                datastore.getAppThemeFlow()
            ) { palette, seedColor, font, materialYou, appTheme ->
                _state.update {
                    it.copy(
                        theme = it.theme.copy(
                            paletteStyle = palette,
                            seedColor = Color(seedColor),
                            font = font,
                            isMaterialYou = materialYou,
                            appTheme = appTheme
                        )
                    )
                }
            }.launchIn(this)

            datastore.getAmoledPref()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(isAmoled = pref)
                        )
                    }
                }
                .launchIn(this)

            datastore.getStartingSectionPref()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            startingSection = pref
                        )
                    }
                }
                .launchIn(this)

            datastore.getBiometricLockPref()
                .onEach { pref ->
                    _state.update {
                        it.copy(isBiometricLockOn = pref)
                    }
                }
                .launchIn(this)
        }
    }

    private suspend fun checkSubscription() {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            SubscriptionResult.Subscribed -> {
                _state.update { it.copy(isUserSubscribed = true) }
            }
            else -> {}
        }
    }
}