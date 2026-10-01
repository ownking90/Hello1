package com.smartmate.ai

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.firebase.FirebaseApp
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.notifications.NotificationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SmartMateApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Only initializes if a valid google-services.json was bundled;
        // otherwise this is a no-op and the app runs in local-only mode.
        runCatching { FirebaseApp.initializeApp(this) }
        NotificationHelper.createChannels(this)
        container = AppContainer(this)
        applyPersistedLocale()
    }

    private fun applyPersistedLocale() {
        runCatching {
            val language = runBlocking { container.settingsRepository.settings.first().language }
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
        }
    }

    companion object {
        private lateinit var instance: SmartMateApp
        fun appContext(): Context = instance.applicationContext
        fun container(): AppContainer = instance.container
    }
}
