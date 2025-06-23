package tech.kjo.kjo_mind_care.ui.main.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.model.NotificationStatus
import tech.kjo.kjo_mind_care.data.model.NotificationType
import tech.kjo.kjo_mind_care.utils.TimeAgoFormatter // Necesitarás un TimeAgoFormatter
import tech.kjo.kjo_mind_care.utils.getCurrentLanguageCode

@Composable
fun NotificationItem(
    notification: Notification,
    onNotificationClick: (String) -> Unit // Callback para navegar al destino
) {
    val languageCode = getCurrentLanguageCode()
    val isNew = notification.status == NotificationStatus.NEW
    val backgroundColor = if (isNew) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
    val textColor = if (isNew) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onNotificationClick(notification.id) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de Notificación (personalizado por tipo)
            NotificationIcon(notificationType = notification.type)
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.getFormattedTitle(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isNew) FontWeight.Bold else FontWeight.Normal,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (isNew) {
                        Spacer(modifier = Modifier.width(8.dp))
                        // Indicador de "Nuevo" (un pequeño círculo o texto)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.getFormattedBody(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = TimeAgoFormatter.format(notification.timestamp, languageCode),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NotificationIcon(notificationType: NotificationType) {
    val icon: ImageVector
    val tint: Color

    when (notificationType) {
        NotificationType.LIKE -> {
            icon = Icons.Default.ThumbUp
            tint = MaterialTheme.colorScheme.primary
        }
        NotificationType.COMMENT -> {
            icon = Icons.Default.Comment
            tint = MaterialTheme.colorScheme.secondary
        }
        NotificationType.MOOD_REMINDER, NotificationType.ACTIVITY_REMINDER -> {
            icon = Icons.Default.Alarm
            tint = MaterialTheme.colorScheme.tertiary
        }
        NotificationType.SYSTEM -> {
            icon = Icons.Default.Info
            tint = MaterialTheme.colorScheme.onSurface
        }
        NotificationType.NEW_BLOG_POST -> {
            icon = Icons.Default.Article
            tint = MaterialTheme.colorScheme.surfaceTint
        }
    }

    Icon(
        imageVector = icon,
        contentDescription = null, // Content description se maneja en el título/cuerpo
        tint = tint,
        modifier = Modifier.size(32.dp)
    )
}