package com.smartmate.ai.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smartmate.ai.domain.AppLanguage
import com.smartmate.ai.domain.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "smartmate_settings")

data class AppSettings(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val notificationsEnabled: Boolean = true
)

/** Persists user-facing app settings (theme, language, notifications) via DataStore. */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_preference")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themePreference = prefs[Keys.THEME]?.let { runCatching { ThemePreference.valueOf(it) }.getOrNull() }
                ?: ThemePreference.SYSTEM,
            language = prefs[Keys.LANGUAGE]?.let { code -> AppLanguage.entries.find { it.code == code } }
                ?: AppLanguage.ENGLISH,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true
        )
    }

    suspend fun setThemePreference(theme: ThemePreference) {
        context.dataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }
}
