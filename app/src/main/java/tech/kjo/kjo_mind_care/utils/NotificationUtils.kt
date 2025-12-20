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
import tech.kjo.kjo_mind_care.ui.navigation.Screen

object NotificationUtils {

    const val CHANNEL_ID = "kjo_mind_care_notifications_channel"
    const val CHANNEL_NAME = "KJO Mind Care Notifications"
    const val CHANNEL_DESCRIPTION = "General notifications for KJO Mind Care app."
    // Extra para pasar el deeplink route
    const val EXTRA_DEEPLINK_ROUTE = "extra_deeplink_route"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
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

        // Crear Intent que apunte directamente a MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            // Añadir flags para manejar correctamente el lanzamiento de la app
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            // Pasar la ruta de destino como extra
            putExtra(EXTRA_DEEPLINK_ROUTE, notification.targetRoute)
            if (notification.targetRoute.isNotBlank()) {
                val deepLinkUri = Uri.parse(
                    "${Screen.MainAppScreen.route}?deepLinkRoute=" + Uri.encode(notification.targetRoute)
                )
                data = deepLinkUri
            }
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

        val formattedTitle = notification.getFormattedTitle(context)
        val formattedBody = notification.getFormattedBody(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_kjo)
            .setContentTitle(formattedTitle)
            .setContentText(formattedBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(formattedBody))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setWhen(notification.timestamp.toDate().time)
            .setShowWhen(true)

        notificationManager.notify(notification.id.hashCode(), builder.build())
    }

    // Las funciones de ayuda privadas ya no son necesarias, se han eliminado.
}
