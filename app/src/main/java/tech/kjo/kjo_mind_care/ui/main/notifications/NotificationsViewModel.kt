package tech.kjo.kjo_mind_care.ui.main.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.repository.NotificationRepository
import tech.kjo.kjo_mind_care.utils.NotificationUtils

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showMarkAllAsReadConfirmation: Boolean = false,
    val isRefreshing: Boolean = false // Nuevo campo para pull to refresh
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
        // Carga inicial o refresco al iniciar
        refreshNotifications()
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

    fun refreshNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            try {
                // Simula una carga de red. En un caso real, esto llamaría a tu backend.
                delay(1500) // Simula una latencia de red
                // Aquí, el repositorio ya está siendo observado, así que solo necesitas asegurarte
                // de que el repositorio tenga lógica para "refrescar" sus datos si vinieran de red.
                // Como es estático, simplemente se actualizará el estado de refreshing.
                // Si tu repo tuviera una función 'fetchNotifications()', la llamarías aquí.
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al recargar notificaciones: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    // Función para simular la adición de una nueva notificación (para pruebas)
    fun addDummyNotification(context: Context, notification: Notification) {
        notificationRepository.addNotification(notification)
        NotificationUtils.showSystemNotification(context, notification)
    }
}