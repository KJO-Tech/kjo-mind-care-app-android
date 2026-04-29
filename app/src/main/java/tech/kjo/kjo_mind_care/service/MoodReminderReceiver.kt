package tech.kjo.kjo_mind_care.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import tech.kjo.kjo_mind_care.MainActivity
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import tech.kjo.kjo_mind_care.utils.NotificationUtils
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.enums.NotificationType
import com.google.firebase.Timestamp

class MoodReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Crear objeto Notification local
        val notificationData = Notification(
            id = "mood_reminder_${System.currentTimeMillis()}",
            type = NotificationType.MOOD_REMINDER,
            targetRoute = Screen.MoodEntryDetail.createRoute(null),
            timestamp = Timestamp.now()
        )

        // Usar NotificationUtils para mostrar la notificación
        NotificationUtils.showSystemNotification(context, notificationData)

        // Reprogramar la alarma para el día siguiente
        val sharedPreferences = context.getSharedPreferences("kjo_mind_care_prefs", Context.MODE_PRIVATE)
        val hour = sharedPreferences.getInt("reminder_hour", 20)
        val minute = sharedPreferences.getInt("reminder_minute", 0)
        NotificationScheduler(context).scheduleDailyMoodReminder(hour, minute)
    }

    companion object {
        // ID ya no es necesario aquí si se genera dinámicamente o se maneja en NotificationUtils
    }
}
