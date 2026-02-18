package com.shub39.grit.core.domain

import com.materialkolor.PaletteStyle
import com.shub39.grit.core.theme.AppTheme
import com.shub39.grit.core.theme.Fonts
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek

interface GritDatastore {
    suspend fun resetAppTheme()

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

    fun getStartingSectionPref(): Flow<Sections>
    suspend fun setStartingPage(page: Sections)

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

    fun getTaskReorderPref(): Flow<Boolean>
    suspend fun setTaskReorderPref(pref: Boolean)

    fun getCompactViewPref(): Flow<Boolean>
    suspend fun setCompactView(pref: Boolean)

    fun getLastChangelogShown(): Flow<String>
    suspend fun updateLastChangelogShown(version: String)
}