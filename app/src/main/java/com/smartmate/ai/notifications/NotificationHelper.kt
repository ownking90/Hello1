package com.smartmate.ai.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smartmate.ai.R

object NotificationHelper {
    const val REMINDER_CHANNEL_ID = "smartmate_reminders"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders you've scheduled in SmartMate AI"
            }
            manager?.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(context: Context, notificationId: Int, title: String, body: String) {
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}
