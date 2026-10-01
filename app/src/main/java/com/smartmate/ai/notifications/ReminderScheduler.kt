package com.smartmate.ai.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.smartmate.ai.data.local.entity.ReminderEntity
import com.smartmate.ai.domain.RepeatOption

/**
 * Schedules Android notifications for [ReminderEntity] rows using
 * [AlarmManager]. `ONCE` reminders fire a single exact alarm; repeating
 * reminders re-schedule themselves from [ReminderReceiver] after each fire.
 */
object ReminderScheduler {

    fun schedule(context: Context, reminder: ReminderEntity) {
        if (!reminder.isEnabled) {
            cancel(context, reminder)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntentFor(context, reminder)
        val triggerAt = reminder.dateTimeEpochMillis
        runCatching {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntentFor(context, reminder))
    }

    fun nextTriggerTime(current: Long, repeatOption: RepeatOption): Long = when (repeatOption) {
        RepeatOption.ONCE -> current
        RepeatOption.DAILY -> current + ONE_DAY_MILLIS
        RepeatOption.WEEKLY -> current + ONE_DAY_MILLIS * 7
        RepeatOption.MONTHLY -> current + ONE_DAY_MILLIS * 30
    }

    private fun pendingIntentFor(context: Context, reminder: ReminderEntity): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderReceiver.EXTRA_TITLE, reminder.title)
            putExtra(ReminderReceiver.EXTRA_BODY, reminder.note)
            putExtra(ReminderReceiver.EXTRA_REPEAT, reminder.repeatOption.name)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L
}
