package com.smartmate.ai.data

import android.content.Context
import com.smartmate.ai.ai.AiService
import com.smartmate.ai.ai.LocalAiService
import com.smartmate.ai.data.auth.AuthRepository
import com.smartmate.ai.data.local.AppDatabase
import com.smartmate.ai.data.repository.ChatRepository
import com.smartmate.ai.data.repository.NoteRepository
import com.smartmate.ai.data.repository.ReminderRepository
import com.smartmate.ai.data.repository.TaskRepository
import com.smartmate.ai.data.settings.SettingsRepository

/**
 * Minimal hand-rolled dependency container (no DI framework) wiring Room,
 * DataStore, auth, and the AI service together for the view models.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)

    val taskRepository = TaskRepository(database.taskDao())
    val noteRepository = NoteRepository(database.noteDao())
    val reminderRepository = ReminderRepository(context, database.reminderDao())
    val chatRepository = ChatRepository(database.chatDao())
    val settingsRepository = SettingsRepository(context)
    val authRepository = AuthRepository()
    val aiService: AiService = LocalAiService()
}
