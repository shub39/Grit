package com.shub39.grit.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.presentation.settings.SettingsAction
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
    stateLayer: StateLayer,
    private val datastore: GritDatastore
): ViewModel() {

    private var observeJob: Job? = null

    private val _state = stateLayer.settingsState

    val state = _state.asStateFlow()
        .onStart { observeJob() }
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
        }
    }

    private fun observeJob() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
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
        }
    }

}