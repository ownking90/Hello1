package com.smartmate.ai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.local.entity.NoteEntity
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val todaysTasks: List<TaskEntity> = emptyList(),
    val upcomingReminders: List<ReminderEntity> = emptyList(),
    val recentNotes: List<NoteEntity> = emptyList(),
    val greeting: DateTimeUtils.Greeting = DateTimeUtils.greetingForHour(),
    val quickChatDraft: String = ""
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                container.taskRepository.observeTasks(),
                container.reminderRepository.observeReminders(),
                container.noteRepository.observeNotes()
            ) { tasks, reminders, notes ->
                HomeUiState(
                    isLoading = false,
                    todaysTasks = tasks.filter { !it.isCompleted && it.dueAtEpochMillis?.let(DateTimeUtils::isToday) == true },
                    upcomingReminders = reminders.filter { DateTimeUtils.isUpcoming(it.dateTimeEpochMillis) }.take(5),
                    recentNotes = notes.take(5)
                )
            }.collect { state -> _uiState.value = state.copy(quickChatDraft = _uiState.value.quickChatDraft) }
        }
    }

    fun onQuickChatDraftChange(text: String) {
        _uiState.value = _uiState.value.copy(quickChatDraft = text)
    }

    /** Sends the home quick-chat box text as a new message and clears the draft. */
    fun sendQuickChatMessage() {
        val text = _uiState.value.quickChatDraft.trim()
        if (text.isBlank()) return
        _uiState.value = _uiState.value.copy(quickChatDraft = "")
        viewModelScope.launch {
            val chat = container.chatRepository.createChat(title = text.take(30))
            container.chatRepository.addMessage(chat.id, text, isFromUser = true)
            val reply = container.aiService.chatReply(emptyList(), text)
            container.chatRepository.addMessage(chat.id, reply, isFromUser = false)
        }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(container) as T
        }
    }
}
