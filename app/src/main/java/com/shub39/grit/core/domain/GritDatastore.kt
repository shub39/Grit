package com.shub39.grit.core.domain

import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface GritDatastore {
    fun getAppThemeFlow(): Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)

    fun getSeedColorFlow(): Flow<Int>
    suspend fun setSeedColor(color: Int)

    fun getAmoledPref(): Flow<Boolean>
    suspend fun setAmoledPref(pref: Boolean)

    fun getPaletteStyle(): Flow<PaletteStyle>
    suspend fun setPaletteStyle(style: PaletteStyle)

    fun getStartOfTheWeekPref(): Flow<DayOfWeek>
    suspend fun setStartOfWeek(day: DayOfWeek)

    fun getStartingPagePref(): Flow<Pages>
    suspend fun setStartingPage(page: Pages)

    fun getIs24Hr(): Flow<Boolean>
    suspend fun setIs24Hr(pref: Boolean)

    fun getMaterialYouFlow(): Flow<Boolean>
    suspend fun setMaterialYou(pref: Boolean)

    fun getNotificationsFlow(): Flow<Boolean>
    suspend fun setNotifications(pref: Boolean)

    fun getFontPrefFlow(): Flow<Fonts>
    suspend fun setFontPref(font: Fonts)

    fun getBiometricLockPref(): Flow<Boolean>
    suspend fun setBiometricPref(pref: Boolean)
}