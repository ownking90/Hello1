package com.smartmate.ai.data.repository

import android.content.Context
import com.smartmate.ai.data.local.dao.ReminderDao
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val context: Context,
    private val reminderDao: ReminderDao
) {
    fun observeReminders(): Flow<List<ReminderEntity>> = reminderDao.observeAll()

    suspend fun saveReminder(reminder: ReminderEntity) {
        reminderDao.upsert(reminder)
        ReminderScheduler.schedule(context, reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        ReminderScheduler.cancel(context, reminder)
        reminderDao.delete(reminder)
    }

    suspend fun setEnabled(reminder: ReminderEntity, enabled: Boolean) {
        val updated = reminder.copy(isEnabled = enabled)
        reminderDao.upsert(updated)
        if (enabled) ReminderScheduler.schedule(context, updated) else ReminderScheduler.cancel(context, updated)
    }
}
