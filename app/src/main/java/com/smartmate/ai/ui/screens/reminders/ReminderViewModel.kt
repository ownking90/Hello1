package com.smartmate.ai.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.domain.RepeatOption
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(private val container: AppContainer) : ViewModel() {

    val reminders: StateFlow<List<ReminderEntity>> = container.reminderRepository.observeReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveReminder(title: String, note: String, dateTime: Long, repeat: RepeatOption) {
        if (title.isBlank()) return
        viewModelScope.launch {
            container.reminderRepository.saveReminder(
                ReminderEntity(title = title, note = note, dateTimeEpochMillis = dateTime, repeatOption = repeat)
            )
        }
    }

    fun setEnabled(reminder: ReminderEntity, enabled: Boolean) {
        viewModelScope.launch { container.reminderRepository.setEnabled(reminder, enabled) }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch { container.reminderRepository.deleteReminder(reminder) }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ReminderViewModel(container) as T
        }
    }
}
