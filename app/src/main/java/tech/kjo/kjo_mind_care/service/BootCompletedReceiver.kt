package tech.kjo.kjo_mind_care.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPreferences = context.getSharedPreferences("kjo_mind_care_prefs", Context.MODE_PRIVATE)
            val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)

            if (notificationsEnabled) {
                val hour = sharedPreferences.getInt("reminder_hour", 20)
                val minute = sharedPreferences.getInt("reminder_minute", 0)
                NotificationScheduler(context).scheduleDailyMoodReminder(hour, minute)
            }
        }
    }
}
