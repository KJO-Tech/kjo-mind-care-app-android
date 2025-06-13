package tech.kjo.kjo_mind_care.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import tech.kjo.kjo_mind_care.MainActivity
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.model.NotificationType
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import java.time.format.DateTimeFormatter
import java.util.Locale

object NotificationUtils {

    const val CHANNEL_ID = "kjo_mind_care_notifications_channel"
    const val CHANNEL_NAME = "KJO Mind Care Notifications"
    const val CHANNEL_DESCRIPTION = "General notifications for KJO Mind Care app."

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // Puedes ajustar esto
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSystemNotification(context: Context, notification: Notification) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        // Modifica la URI del deep link para que MainAppScreen pueda procesarla correctamente
        // El deep link real de la notificación debe apuntar a la ruta global que MainAppScreen puede manejar
        val deepLinkUri = Uri.parse(
            "${Screen.MainAppScreen.route}?deepLinkRoute=" + Uri.encode(notification.targetRoute)
        )

        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            setPackage(context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                notification.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                notification.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val formattedTime = notification.timestamp.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_kjo)
            .setContentTitle(notification.getFormattedTitle())
            .setContentText(notification.getFormattedBody())
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.getFormattedBody()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setWhen(notification.timestamp.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setShowWhen(true)

        notificationManager.notify(notification.id.hashCode(), builder.build())
    }
}