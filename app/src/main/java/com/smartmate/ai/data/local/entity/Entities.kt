package com.smartmate.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartmate.ai.domain.Category
import com.smartmate.ai.domain.Priority
import com.smartmate.ai.domain.RepeatOption
import java.util.UUID

/**
 * Local Room entities mirroring the cloud Firestore collections described in
 * [com.smartmate.ai.data.remote.FirestoreSchema]. Each entity keeps an [ownerId]
 * so a future sync worker can scope reads/writes per signed-in user.
 */

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "local",
    val title: String,
    val notes: String = "",
    val dueAtEpochMillis: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.PERSONAL,
    val isCompleted: Boolean = false,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "local",
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val category: Category = Category.PERSONAL,
    val tags: String = "",
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "local",
    val title: String,
    val note: String = "",
    val dateTimeEpochMillis: Long,
    val repeatOption: RepeatOption = RepeatOption.ONCE,
    val isEnabled: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "local",
    val title: String = "New Chat",
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val isFromUser: Boolean,
    val text: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val ownerId: String = "local",
    val themePreference: String = "SYSTEM",
    val language: String = "en",
    val notificationsEnabled: Boolean = true
)
