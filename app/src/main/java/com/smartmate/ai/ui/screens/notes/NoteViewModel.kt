package com.smartmate.ai.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.local.entity.NoteEntity
import com.smartmate.ai.domain.AiToolType
import com.smartmate.ai.domain.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotesUiState(
    val query: String = "",
    val notes: List<NoteEntity> = emptyList()
) {
    val filtered: List<NoteEntity> get() = if (query.isBlank()) notes else notes.filter {
        it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
    }
}

class NoteViewModel(private val container: AppContainer) : ViewModel() {

    private val _query = MutableStateFlow("")

    val uiState: StateFlow<NotesUiState> = combine(
        container.noteRepository.observeNotes(),
        _query
    ) { notes, query -> NotesUiState(query, notes) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesUiState())

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun saveNote(title: String, content: String, category: Category, tags: String, existing: NoteEntity? = null) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            val note = existing?.copy(title = title, content = content, category = category, tags = tags)
                ?: NoteEntity(title = title, content = content, category = category, tags = tags)
            container.noteRepository.saveNote(note)
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch { container.noteRepository.togglePin(note) }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { container.noteRepository.deleteNote(note) }
    }

    fun improveWithAi(note: NoteEntity, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val improved = container.aiService.runTool(AiToolType.GRAMMAR_FIXER, note.content)
            onResult(improved)
        }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = NoteViewModel(container) as T
        }
    }
}
