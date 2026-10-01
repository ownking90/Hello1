package com.smartmate.ai.ui.screens.tasks

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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.domain.Category
import com.smartmate.ai.domain.Priority
import com.smartmate.ai.ui.common.ConfirmDialog
import com.smartmate.ai.ui.common.EmptyState

private fun taskTabLabel(tab: TaskTab): Int = when (tab) {
    TaskTab.TODAY -> R.string.tasks_today
    TaskTab.UPCOMING -> R.string.tasks_upcoming
    TaskTab.COMPLETED -> R.string.tasks_completed
}

private fun priorityLabel(priority: Priority): Int = when (priority) {
    Priority.LOW -> R.string.priority_low
    Priority.MEDIUM -> R.string.priority_medium
    Priority.HIGH -> R.string.priority_high
}

private fun categoryLabel(category: Category): Int = when (category) {
    Category.STUDY -> R.string.category_study
    Category.WORK -> R.string.category_work
    Category.PERSONAL -> R.string.category_personal
    Category.OTHER -> R.string.category_other
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(container: AppContainer) {
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var taskPendingDelete by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                TaskTab.entries.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(stringResource(taskTabLabel(tab))) }
                    )
                }
            }

            if (state.visibleTasks.isEmpty()) {
                EmptyState(stringResource(R.string.empty_state_tasks))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(state.visibleTasks, key = { it.id }) { task ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.setCompleted(task, it) }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title)
                                    Text("${stringResource(priorityLabel(task.priority))} • ${stringResource(categoryLabel(task.category))}")
                                }
                                IconButton(onClick = { taskPendingDelete = task }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, notes, priority, category ->
                viewModel.saveTask(title, notes, null, priority, category)
                showAddDialog = false
            }
        )
    }

    taskPendingDelete?.let { task ->
        ConfirmDialog(
            title = stringResource(R.string.confirm_delete_title),
            message = stringResource(R.string.confirm_delete_message),
            confirmLabel = stringResource(R.string.delete),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = {
                viewModel.deleteTask(task)
                taskPendingDelete = null
            },
            onDismiss = { taskPendingDelete = null }
        )
    }
}

@Composable
private fun AddEditTaskDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, notes: String, priority: Priority, category: Category) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var category by remember { mutableStateOf(Category.PERSONAL) }
    var titleError by remember { mutableStateOf<String?>(null) }
    val titleRequiredError = stringResource(R.string.error_title_required)

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_task)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = null },
                    label = { Text(stringResource(R.string.field_title)) },
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } }
                )
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.field_notes)) })
                Text(stringResource(R.string.field_priority))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { p ->
                        androidx.compose.material3.FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(stringResource(priorityLabel(p))) }
                        )
                    }
                }
                Text(stringResource(R.string.field_category))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Category.entries.forEach { c ->
                        androidx.compose.material3.FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(stringResource(categoryLabel(c))) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                if (title.isBlank()) {
                    titleError = titleRequiredError
                } else {
                    onSave(title, notes, priority, category)
                }
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { androidx.compose.material3.TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
