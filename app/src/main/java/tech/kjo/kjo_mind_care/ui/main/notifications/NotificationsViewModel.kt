package tech.kjo.kjo_mind_care.ui.main.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.usecase.notification.GetNotificationsUseCase
import tech.kjo.kjo_mind_care.usecase.notification.MarkAllNotificationsAsReadUseCase
import tech.kjo.kjo_mind_care.usecase.notification.MarkNotificationAsReadUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showMarkAllAsReadConfirmation: Boolean = false,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = getCurrentUserUseCase()
            if (user != null) {
                _userId.value = user.uid
                getNotificationsUseCase(user.uid).collect { notifications ->
                    _uiState.update {
                        it.copy(
                            notifications = notifications,
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(error = "User not found", isLoading = false) }
            }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            _userId.value?.let {
                markNotificationAsReadUseCase(it, notificationId).onFailure {
                    _uiState.update { state -> state.copy(error = it.message) }
                }
            }
        }
    }

    fun showMarkAllAsReadConfirmation(show: Boolean) {
        _uiState.update { it.copy(showMarkAllAsReadConfirmation = show) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _userId.value?.let { userId ->
                markAllNotificationsAsReadUseCase(userId).onSuccess {
                    _uiState.update { it.copy(showMarkAllAsReadConfirmation = false) }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(
                            error = it.message,
                            showMarkAllAsReadConfirmation = false
                        )
                    }
                }
            }
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            delay(1000)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}
