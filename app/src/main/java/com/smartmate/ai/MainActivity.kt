package com.smartmate.ai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.smartmate.ai.data.settings.AppSettings
import com.smartmate.ai.domain.ThemePreference
import com.smartmate.ai.navigation.SmartMateNavHost
import com.smartmate.ai.ui.theme.SmartMateAITheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op: handled via system UI */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()

        val container = (application as SmartMateApp).container

        setContent {
            val settings by container.settingsRepository.settings.collectAsState(initial = AppSettings())
            val darkTheme = when (settings.themePreference) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                ThemePreference.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            SmartMateAITheme(useDarkTheme = darkTheme) {
                SmartMateNavHost(container = container)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
