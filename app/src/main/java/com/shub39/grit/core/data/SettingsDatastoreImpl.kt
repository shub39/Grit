/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.grit.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.grit.core.domain.Sections
import com.shub39.grit.core.domain.SettingsDatastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import org.koin.core.annotation.Single

@Single(binds = [SettingsDatastore::class])
class SettingsDatastoreImpl(private val datastore: DataStore<Preferences>) : SettingsDatastore {

    companion object {
        private val startOfWeekKey = stringPreferencesKey("start_of_week")
        private val startingSectionKey = stringPreferencesKey("starting_page")
        private val is24HrKey = booleanPreferencesKey("is_24Hr")
        private val notificationsKey = booleanPreferencesKey("notifications")
        private val biometricLockKey = booleanPreferencesKey("biometric")
        private val taskReorderKey = booleanPreferencesKey("task_reorder")
        private val compactHabitView = booleanPreferencesKey("compact_habit_view")
        private val lastChangelogShownKey = stringPreferencesKey("last_changelog_shown")
    }

    override fun getStartOfTheWeekPref(): Flow<DayOfWeek> =
        datastore.data.map { prefs ->
            val dayOfWeek = prefs[startOfWeekKey] ?: DayOfWeek.MONDAY.name
            return@map DayOfWeek.valueOf(dayOfWeek)
        }

    override suspend fun setStartOfWeek(day: DayOfWeek) {
        datastore.edit { prefs -> prefs[startOfWeekKey] = day.name }
    }

    override fun getStartingSectionPref(): Flow<Sections> =
        datastore.data.map { pref ->
            val page = pref[startingSectionKey] ?: Sections.Tasks.name
            return@map Sections.valueOf(page)
        }

    override suspend fun setStartingPage(page: Sections) {
        datastore.edit { prefs -> prefs[startingSectionKey] = page.name }
    }

    override fun getIs24Hr(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[is24HrKey] == true }

    override suspend fun setIs24Hr(pref: Boolean) {
        datastore.edit { prefs -> prefs[is24HrKey] = pref }
    }

    override fun getNotificationsFlow(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[notificationsKey] == true }

    override suspend fun setNotifications(pref: Boolean) {
        datastore.edit { prefs -> prefs[notificationsKey] = pref }
    }

    override fun getBiometricLockPref(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[biometricLockKey] == true }

    override suspend fun setBiometricPref(pref: Boolean) {
        datastore.edit { prefs -> prefs[biometricLockKey] = pref }
    }

    override fun getTaskReorderPref(): Flow<Boolean> =
        datastore.data.map { prefs -> prefs[taskReorderKey] ?: true }

    override suspend fun setTaskReorderPref(pref: Boolean) {
        datastore.edit { prefs -> prefs[taskReorderKey] = pref }
    }

    override fun getCompactViewPref(): Flow<Boolean> =
        datastore.data.map { pref -> pref[compactHabitView] ?: false }

    override suspend fun setCompactView(pref: Boolean) {
        datastore.edit { prefs -> prefs[compactHabitView] = pref }
    }

    override fun getLastChangelogShown(): Flow<String> =
        datastore.data.map { prefs -> prefs[lastChangelogShownKey] ?: "" }

    override suspend fun updateLastChangelogShown(version: String) {
        datastore.edit { settings -> settings[lastChangelogShownKey] = version }
    }
}
