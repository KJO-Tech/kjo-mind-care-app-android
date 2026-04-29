package tech.kjo.kjo_mind_care.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import tech.kjo.kjo_mind_care.R
import java.util.Calendar

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleDailyMoodReminder(hour: Int, minute: Int) {
        val intent = Intent(context, MoodReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            MOOD_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0) // Importante: Limpiar milisegundos para evitar disparos inmediatos si coincide el segundo
            
            // Si la hora programada es anterior o igual a "ahora", programar para mañana.
            // Usamos <= para ser estrictos y evitar bucles si se programa exactamente en el mismo minuto.
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelDailyMoodReminder() {
        val intent = Intent(context, MoodReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            MOOD_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    companion object {
        private const val MOOD_REMINDER_REQUEST_CODE = 1001
    }
}
