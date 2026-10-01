package com.smartmate.ai.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.local.entity.NoteEntity
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlannerUiState(
    val todaysTasks: List<TaskEntity> = emptyList(),
    val todaysReminders: List<ReminderEntity> = emptyList(),
    val recentNotes: List<NoteEntity> = emptyList(),
    val aiPlan: String? = null,
    val isGeneratingPlan: Boolean = false
)

class PlannerViewModel(private val container: AppContainer) : ViewModel() {

    private val _aiPlan = MutableStateFlow<String?>(null)
    private val _isGenerating = MutableStateFlow(false)

    val uiState: StateFlow<PlannerUiState> = combine(
        container.taskRepository.observeTasks(),
        container.reminderRepository.observeReminders(),
        container.noteRepository.observeNotes(),
        _aiPlan,
        _isGenerating
    ) { tasks, reminders, notes, plan, generating ->
        PlannerUiState(
            todaysTasks = tasks.filter { !it.isCompleted && it.dueAtEpochMillis?.let(DateTimeUtils::isToday) != false },
            todaysReminders = reminders.filter { DateTimeUtils.isToday(it.dateTimeEpochMillis) },
            recentNotes = notes.take(5),
            aiPlan = plan,
            isGeneratingPlan = generating
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlannerUiState())

    fun generatePlan() {
        viewModelScope.launch {
            _isGenerating.value = true
            val pendingTitles = uiState.value.todaysTasks.map { it.title }
            _aiPlan.value = container.aiService.generateDailyPlan(pendingTitles)
            _isGenerating.value = false
        }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PlannerViewModel(container) as T
        }
    }
}
