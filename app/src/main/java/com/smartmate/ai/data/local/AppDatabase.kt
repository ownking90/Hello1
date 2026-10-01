package com.smartmate.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smartmate.ai.data.local.dao.ChatDao
import com.smartmate.ai.data.local.dao.NoteDao
import com.smartmate.ai.data.local.dao.ReminderDao
import com.smartmate.ai.data.local.dao.TaskDao
import com.smartmate.ai.data.local.dao.UserSettingsDao
import com.smartmate.ai.data.local.entity.ChatEntity
import com.smartmate.ai.data.local.entity.MessageEntity
import com.smartmate.ai.data.local.entity.NoteEntity
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.data.local.entity.UserSettingsEntity

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        ReminderEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao
    abstract fun chatDao(): ChatDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartmate-ai.db"
                ).build().also { INSTANCE = it }
            }
    }
}
