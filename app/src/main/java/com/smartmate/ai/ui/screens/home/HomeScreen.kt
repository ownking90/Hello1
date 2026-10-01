package com.smartmate.ai.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartmate.ai.R
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.ui.common.LoadingState
import com.smartmate.ai.ui.common.SectionCard
import com.smartmate.ai.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    container: AppContainer,
    onOpenChat: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenPlanner: () -> Unit = {},
    onOpenAiTools: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingState()
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val greetingText = stringResource(
                when (state.greeting) {
                    DateTimeUtils.Greeting.MORNING -> R.string.greeting_morning
                    DateTimeUtils.Greeting.AFTERNOON -> R.string.greeting_afternoon
                    DateTimeUtils.Greeting.EVENING -> R.string.greeting_evening
                }
            )
            Column {
                Text(greetingText, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                Text(DateTimeUtils.formatDate(System.currentTimeMillis(), "EEEE, dd MMMM"))
            }
        }

        item {
            SectionCard {
                Text(stringResource(R.string.ask_smartmate_ai), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = state.quickChatDraft,
                    onValueChange = viewModel::onQuickChatDraftChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.quick_chat_hint)) }
                )
                Button(onClick = {
                    viewModel.sendQuickChatMessage()
                    onOpenChat()
                }) {
                    Icon(Icons.Filled.Chat, contentDescription = null)
                    Text(" " + stringResource(R.string.ask))
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpenChat) { Text(stringResource(R.string.quick_action_chat)) }
                Button(onClick = onOpenTasks) { Text(stringResource(R.string.quick_action_task)) }
                Button(onClick = onOpenReminders) { Text(stringResource(R.string.quick_action_reminder)) }
                Button(onClick = onOpenNotes) { Text(stringResource(R.string.quick_action_note)) }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpenPlanner) { Text(stringResource(R.string.daily_planner)) }
                Button(onClick = onOpenAiTools) { Text(stringResource(R.string.ai_tools)) }
            }
        }

        item {
            SectionCard {
                Row {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Text(" " + stringResource(R.string.todays_tasks), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                }
                if (state.todaysTasks.isEmpty()) {
                    Text(stringResource(R.string.no_tasks_due_today))
                } else {
                    state.todaysTasks.forEach { task -> Text("• ${task.title}") }
                }
            }
        }

        item {
            SectionCard {
                Row {
                    Icon(Icons.Filled.Notifications, contentDescription = null)
                    Text(" " + stringResource(R.string.upcoming_reminders), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                }
                if (state.upcomingReminders.isEmpty()) {
                    Text(stringResource(R.string.no_upcoming_reminders))
                } else {
                    state.upcomingReminders.forEach { reminder ->
                        Text("• ${reminder.title} — ${DateTimeUtils.formatDateTime(reminder.dateTimeEpochMillis)}")
                    }
                }
            }
        }

        item {
            SectionCard {
                Row {
                    Icon(Icons.Filled.Note, contentDescription = null)
                    Text(" " + stringResource(R.string.recent_notes), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                }
                if (state.recentNotes.isEmpty()) {
                    Text(stringResource(R.string.no_notes_yet))
                } else {
                    state.recentNotes.forEach { note -> Text("• ${note.title}") }
                }
            }
        }
    }
}
