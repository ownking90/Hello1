package com.smartmate.ai.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartmate.ai.R
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.domain.AppLanguage
import com.smartmate.ai.domain.ThemePreference
import com.smartmate.ai.ui.common.ConfirmDialog
import com.smartmate.ai.ui.common.SectionCard

@Composable
fun SettingsScreen(container: AppContainer) {
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(container))
    val settings by viewModel.settings.collectAsState()
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)

        SectionCard {
            Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemePreference.entries.forEach { theme ->
                    FilterChip(
                        selected = settings.themePreference == theme,
                        onClick = { viewModel.setTheme(theme) },
                        label = { Text(theme.name) }
                    )
                }
            }
        }

        SectionCard {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { language ->
                    FilterChip(
                        selected = settings.language == language,
                        onClick = { viewModel.setLanguage(language) },
                        label = { Text(if (language == AppLanguage.ENGLISH) "English" else "हिंदी") }
                    )
                }
            }
        }

        SectionCard {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.notifications), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Switch(checked = settings.notificationsEnabled, onCheckedChange = viewModel::setNotificationsEnabled)
            }
        }

        SectionCard {
            TextButton(onClick = { showClearHistoryDialog = true }) { Text(stringResource(R.string.clear_chat_history)) }
            TextButton(onClick = { showPrivacyDialog = true }) { Text(stringResource(R.string.privacy_policy)) }
            TextButton(onClick = { showAboutDialog = true }) { Text(stringResource(R.string.about_app)) }
        }
    }

    if (showClearHistoryDialog) {
        ConfirmDialog(
            title = stringResource(R.string.clear_chat_history),
            message = stringResource(R.string.confirm_delete_message),
            confirmLabel = stringResource(R.string.delete),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = { viewModel.clearChatHistory(); showClearHistoryDialog = false },
            onDismiss = { showClearHistoryDialog = false }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.about_app)) },
            text = { Text(stringResource(R.string.about_app_body)) },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text(stringResource(R.string.ok)) } }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.privacy_policy)) },
            text = { Text(stringResource(R.string.privacy_policy_body)) },
            confirmButton = { TextButton(onClick = { showPrivacyDialog = false }) { Text(stringResource(R.string.ok)) } }
        )
    }
}
