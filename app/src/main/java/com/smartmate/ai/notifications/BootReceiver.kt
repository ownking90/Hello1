package com.smartmate.ai.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-creates notification channels after a device reboot. Rescheduling of
 * individual reminder alarms (which are cleared on reboot) happens in
 * MainActivity on next app launch by re-reading enabled reminders from Room.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationHelper.createChannels(context)
        }
    }
}
