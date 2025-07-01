package com.shub39.grit.viewmodels

import androidx.lifecycle.ViewModel

class MainViewModel() : ViewModel() {
    private var _appUnlocked = false

    fun isAppUnlocked(): Boolean = _appUnlocked

    fun setAppUnlocked(value: Boolean) {
        _appUnlocked = value
    }
}