package com.smartmate.ai.ui.screens.notes

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.smartmate.ai.data.local.entity.NoteEntity
import com.smartmate.ai.domain.Category
import com.smartmate.ai.ui.common.ConfirmDialog
import com.smartmate.ai.ui.common.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(container: AppContainer) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteEntity?>(null) }
    var notePendingDelete by remember { mutableStateOf<NoteEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                label = { Text(stringResource(R.string.search_notes)) }
            )

            if (state.filtered.isEmpty()) {
                EmptyState(stringResource(R.string.empty_state_notes))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(state.filtered, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row {
                                    Text(note.title, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.togglePin(note) }) {
                                        Icon(Icons.Filled.PushPin, contentDescription = stringResource(R.string.pin_note))
                                    }
                                    IconButton(onClick = { notePendingDelete = note }) {
                                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                                    }
                                }
                                Text(note.content, maxLines = 3)
                                Row {
                                    TextButton(onClick = { noteToEdit = note }) { Text(stringResource(R.string.edit)) }
                                    TextButton(onClick = {
                                        viewModel.improveWithAi(note) { improved ->
                                            viewModel.saveNote(note.title, improved, note.category, note.tags, note)
                                        }
                                    }) {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                                        Text(" " + stringResource(R.string.ai_improve_note))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || noteToEdit != null) {
        AddEditNoteDialog(
            existing = noteToEdit,
            onDismiss = { showAddDialog = false; noteToEdit = null },
            onSave = { title, content, category, tags ->
                viewModel.saveNote(title, content, category, tags, noteToEdit)
                showAddDialog = false
                noteToEdit = null
            }
        )
    }

    notePendingDelete?.let { note ->
        ConfirmDialog(
            title = stringResource(R.string.confirm_delete_title),
            message = stringResource(R.string.confirm_delete_message),
            confirmLabel = stringResource(R.string.delete),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = { viewModel.deleteNote(note); notePendingDelete = null },
            onDismiss = { notePendingDelete = null }
        )
    }
}

@Composable
private fun AddEditNoteDialog(
    existing: NoteEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: Category, tags: String) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var content by remember { mutableStateOf(existing?.content.orEmpty()) }
    var tags by remember { mutableStateOf(existing?.tags.orEmpty()) }
    var titleError by remember { mutableStateOf<String?>(null) }
    val titleRequiredError = stringResource(R.string.error_title_required)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) stringResource(R.string.add_note) else stringResource(R.string.edit)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = null },
                    label = { Text(stringResource(R.string.field_title)) },
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } }
                )
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.field_content)) })
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text(stringResource(R.string.field_tags)) })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    titleError = titleRequiredError
                } else {
                    onSave(title, content, existing?.category ?: Category.PERSONAL, tags)
                }
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
