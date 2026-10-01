package com.smartmate.ai.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartmate.ai.domain.RepeatOption

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val body = intent.getStringExtra(EXTRA_BODY).orEmpty()
        val repeat = RepeatOption.valueOf(intent.getStringExtra(EXTRA_REPEAT) ?: RepeatOption.ONCE.name)

        NotificationHelper.showReminderNotification(context, reminderId.hashCode(), title, body)

        if (repeat != RepeatOption.ONCE) {
            // Re-schedule the next occurrence; the ViewModel layer keeps the
            // Room row's dateTimeEpochMillis in sync on next app launch.
            val next = ReminderScheduler.nextTriggerTime(System.currentTimeMillis(), repeat)
            com.smartmate.ai.data.local.entity.ReminderEntity(
                id = reminderId,
                title = title,
                note = body,
                dateTimeEpochMillis = next,
                repeatOption = repeat
            ).let { ReminderScheduler.schedule(context, it) }
        }
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_REPEAT = "extra_repeat"
    }
}
