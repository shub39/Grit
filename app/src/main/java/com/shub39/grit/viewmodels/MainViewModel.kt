package com.shub39.grit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel : ViewModel() {
    private var _state = MutableStateFlow(false)

    val isAppUnlocked = _state.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun setAppUnlocked(value: Boolean) {
        _state.update { value }
    }
}