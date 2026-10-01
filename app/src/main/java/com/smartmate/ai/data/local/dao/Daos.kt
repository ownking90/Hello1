package com.smartmate.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartmate.ai.data.local.entity.ChatEntity
import com.smartmate.ai.data.local.entity.MessageEntity
import com.smartmate.ai.data.local.entity.NoteEntity
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueAtEpochMillis ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: String)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteById(noteId: String)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY dateTimeEpochMillis ASC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteById(reminderId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(chat: ChatEntity)

    @Delete
    suspend fun delete(chat: ChatEntity)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteById(chatId: String)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAtEpochMillis ASC")
    fun observeMessages(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

    @Query("UPDATE chats SET updatedAtEpochMillis = :timestamp WHERE id = :chatId")
    suspend fun touchUpdatedAt(chatId: String, timestamp: Long)

    @Query("DELETE FROM chats")
    suspend fun deleteAllChats()

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE ownerId = :ownerId LIMIT 1")
    fun observe(ownerId: String = "local"): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: UserSettingsEntity)
}
