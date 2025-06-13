package tech.kjo.kjo_mind_care.ui.main.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.repository.NotificationRepository

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showMarkAllAsReadConfirmation: Boolean = false
)

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        // Observar las notificaciones del repositorio
        viewModelScope.launch {
            notificationRepository.notifications.collect { notifications ->
                _uiState.update { it.copy(notifications = notifications) }
            }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        notificationRepository.markNotificationAsRead(notificationId)
    }

    fun showMarkAllAsReadConfirmation(show: Boolean) {
        _uiState.update { it.copy(showMarkAllAsReadConfirmation = show) }
    }

    fun markAllAsRead() {
        notificationRepository.markAllNotificationsAsRead()
        _uiState.update { it.copy(showMarkAllAsReadConfirmation = false) } // Cierra el diálogo
    }

    // Función para simular la adición de una nueva notificación (para pruebas)
    fun addDummyNotification(notification: Notification) {
        notificationRepository.addNotification(notification)
    }
}