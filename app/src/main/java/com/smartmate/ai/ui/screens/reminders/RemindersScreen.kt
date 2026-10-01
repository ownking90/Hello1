package com.smartmate.ai.ui.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.smartmate.ai.domain.RepeatOption
import com.smartmate.ai.ui.common.EmptyState
import com.smartmate.ai.util.DateTimeUtils
import java.util.Calendar

private fun repeatLabel(option: RepeatOption): Int = when (option) {
    RepeatOption.ONCE -> R.string.repeat_once
    RepeatOption.DAILY -> R.string.repeat_daily
    RepeatOption.WEEKLY -> R.string.repeat_weekly
    RepeatOption.MONTHLY -> R.string.repeat_monthly
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(container: AppContainer) {
    val viewModel: ReminderViewModel = viewModel(factory = ReminderViewModel.factory(container))
    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_reminder))
            }
        }
    ) { padding ->
        if (reminders.isEmpty()) {
            EmptyState(stringResource(R.string.empty_state_reminders), Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
                items(reminders, key = { it.id }) { reminder ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(reminder.title)
                                Text(DateTimeUtils.formatDateTime(reminder.dateTimeEpochMillis))
                                Text(stringResource(repeatLabel(reminder.repeatOption)))
                            }
                            Switch(checked = reminder.isEnabled, onCheckedChange = { viewModel.setEnabled(reminder, it) })
                            IconButton(onClick = { viewModel.deleteReminder(reminder) }) {
                                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, note, dateTime, repeat ->
                viewModel.saveReminder(title, note, dateTime, repeat)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, note: String, dateTime: Long, repeat: RepeatOption) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf(RepeatOption.ONCE) }
    var titleError by remember { mutableStateOf<String?>(null) }
    val titleRequiredError = stringResource(R.string.error_title_required)
    // Defaults the reminder to one hour from now; a production build would
    // present native date/time pickers here.
    val defaultDateTime = remember { Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) }.timeInMillis }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_reminder)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = null },
                    label = { Text(stringResource(R.string.field_title)) },
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } }
                )
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text(stringResource(R.string.field_notes)) })
                Text(stringResource(R.string.reminder_fires_at, DateTimeUtils.formatDateTime(defaultDateTime)))
                Text(stringResource(R.string.field_repeat))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RepeatOption.entries.forEach { option ->
                        FilterChip(selected = repeat == option, onClick = { repeat = option }, label = { Text(stringResource(repeatLabel(option))) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    titleError = titleRequiredError
                } else {
                    onSave(title, note, defaultDateTime, repeat)
                }
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
