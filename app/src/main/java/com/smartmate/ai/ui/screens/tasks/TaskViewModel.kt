package com.smartmate.ai.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.domain.Category
import com.smartmate.ai.domain.Priority
import com.smartmate.ai.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskTab { TODAY, UPCOMING, COMPLETED }

data class TasksUiState(
    val selectedTab: TaskTab = TaskTab.TODAY,
    val tasks: List<TaskEntity> = emptyList()
) {
    val visibleTasks: List<TaskEntity> get() = when (selectedTab) {
        TaskTab.TODAY -> tasks.filter { !it.isCompleted && (it.dueAtEpochMillis == null || DateTimeUtils.isToday(it.dueAtEpochMillis)) }
        TaskTab.UPCOMING -> tasks.filter { !it.isCompleted && it.dueAtEpochMillis?.let(DateTimeUtils::isUpcoming) == true }
        TaskTab.COMPLETED -> tasks.filter { it.isCompleted }
    }
}

class TaskViewModel(private val container: AppContainer) : ViewModel() {

    private val _selectedTab = MutableStateFlow(TaskTab.TODAY)

    val uiState: StateFlow<TasksUiState> = combine(
        container.taskRepository.observeTasks(),
        _selectedTab
    ) { tasks, tab -> TasksUiState(tab, tasks) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState())

    fun selectTab(tab: TaskTab) {
        _selectedTab.value = tab
    }

    fun saveTask(title: String, notes: String, dueAt: Long?, priority: Priority, category: Category, existing: TaskEntity? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = existing?.copy(
                title = title,
                notes = notes,
                dueAtEpochMillis = dueAt,
                priority = priority,
                category = category
            ) ?: TaskEntity(title = title, notes = notes, dueAtEpochMillis = dueAt, priority = priority, category = category)
            container.taskRepository.saveTask(task)
        }
    }

    fun setCompleted(task: TaskEntity, completed: Boolean) {
        viewModelScope.launch { container.taskRepository.setCompleted(task, completed) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { container.taskRepository.deleteTask(task) }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(container) as T
        }
    }
}
