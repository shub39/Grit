package com.shub39.grit.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.billing.SubscriptionResult
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.domain.backup.ExportRepo
import com.shub39.grit.core.domain.backup.ExportState
import com.shub39.grit.core.domain.backup.RestoreRepo
import com.shub39.grit.core.domain.backup.RestoreResult
import com.shub39.grit.core.domain.backup.RestoreState
import com.shub39.grit.core.presentation.settings.BackupState
import com.shub39.grit.core.presentation.settings.SettingsAction
import com.shub39.grit.util.Utils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val stateLayer: StateLayer,
    private val billingHandler: BillingHandler,
    private val exportRepo: ExportRepo,
    private val restoreRepo: RestoreRepo,
    private val datastore: GritDatastore
) : ViewModel() {

    private var observeJob: Job? = null

    private val _state = stateLayer.settingsState

    val state = _state.asStateFlow()
        .onStart {
            checkSubscription()
            observeJob()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: SettingsAction) = viewModelScope.launch {
        when (action) {
            is SettingsAction.ChangeAmoled -> datastore.setAmoledPref(action.pref)

            is SettingsAction.ChangeAppTheme -> datastore.setAppTheme(action.appTheme)

            is SettingsAction.ChangeIs24Hr -> datastore.setIs24Hr(action.pref)

            is SettingsAction.ChangeMaterialYou -> datastore.setMaterialYou(action.pref)

            is SettingsAction.ChangePaletteStyle -> datastore.setPaletteStyle(action.style)

            is SettingsAction.ChangeSeedColor -> datastore.setSeedColor(action.color.toArgb())

            is SettingsAction.ChangeStartOfTheWeek -> datastore.setStartOfWeek(action.pref)

            is SettingsAction.ChangeStartingPage -> datastore.setStartingPage(action.page)

            SettingsAction.OnResetBackupState -> {
                _state.update { it.copy(backupState = BackupState()) }
            }

            SettingsAction.OnExport -> {
                _state.update {
                    it.copy(
                        backupState = it.backupState.copy(
                            exportState = ExportState.EXPORTING
                        )
                    )
                }

                exportRepo.exportToJson()

                _state.update {
                    it.copy(
                        backupState = it.backupState.copy(
                            exportState = ExportState.EXPORTED
                        )
                    )
                }
            }

            is SettingsAction.OnRestore -> {
                _state.update {
                    it.copy(
                        backupState = it.backupState.copy(
                            restoreState = RestoreState.RESTORING
                        )
                    )
                }

                val result = restoreRepo.restoreData(action.uri)

                _state.update {
                    it.copy(
                        backupState = it.backupState.copy(
                            restoreState = when (result) {
                                is RestoreResult.Failure -> RestoreState.FAILURE
                                RestoreResult.Success -> RestoreState.RESTORED
                            }
                        )
                    )
                }
            }

            is SettingsAction.ChangePauseNotifications -> datastore.setNotifications(action.pref)

            is SettingsAction.ChangeFontPref -> datastore.setFontPref(action.font)

            is SettingsAction.ChangeBiometricLock -> datastore.setBiometricPref(action.pref)

            is SettingsAction.OnCheckBiometric -> {
                _state.update {
                    it.copy(
                        biometricAvailable = Utils.authenticationAvailable(action.context)
                    )
                }
            }

            SettingsAction.OnPaywallDismiss -> {
                _state.update {
                    it.copy(
                        showPaywall = false
                    )
                }

                checkSubscription()
            }

            SettingsAction.OnCheckSubscription -> checkSubscription()

            SettingsAction.OnPaywallShow -> _state.update {
                it.copy(
                    showPaywall = true
                )
            }

            SettingsAction.OnResetTheme -> datastore.resetAppTheme()
        }
    }

    private suspend fun checkSubscription() {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            SubscriptionResult.Subscribed -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isUserSubscribed = true
                        )
                    }

                    stateLayer.habitsState.update {
                        it.copy(
                            isUserSubscribed = true
                        )
                    }
                }
            }

            SubscriptionResult.NotSubscribed -> datastore.resetAppTheme()

            else -> {}
        }
    }

    private fun observeJob() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
            datastore.getNotificationsFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            pauseNotifications = pref
                        )
                    }
                }
                .launchIn(this)

            datastore.getAppThemeFlow()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                appTheme = flow
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getFontPrefFlow()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                font = flow
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getSeedColorFlow()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                seedColor = Color(flow)
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getAmoledPref()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                isAmoled = flow
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getMaterialYouFlow()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                isMaterialYou = flow
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getPaletteStyle()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                paletteStyle = flow
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getIs24Hr()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            is24Hr = flow
                        )
                    }
                }
                .launchIn(this)

            datastore.getStartingPagePref()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            startingPage = flow
                        )
                    }
                }
                .launchIn(this)

            datastore.getStartOfTheWeekPref()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            startOfTheWeek = flow
                        )
                    }
                }
                .launchIn(this)

            datastore.getBiometricLockPref()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            biometric = flow
                        )
                    }
                }
                .launchIn(this)
        }
    }

}