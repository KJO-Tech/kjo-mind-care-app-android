package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.usecase.user.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            photoUrl = "https://avatars.githubusercontent.com/u/102635058?v=4",
            name = "Ryan",
            email = "ryan@gmail.com",
            checkIns = 28,
            posts = 12,
            badges = 3,
            darkMode = true,
            isLoggingOut = false,
            logoutError = null
        )
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Result<Unit>>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Result<Unit>> = _logoutEvent.asSharedFlow()

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(notifications = enabled) }

        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(darkMode = enabled) }
            saveThemePreference(enabled)
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {

    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true, logoutError = null) }
            val result = logoutUseCase()
            result.onSuccess {
                _logoutEvent.emit(Result.success(Unit))
            }.onFailure { throwable ->
                _uiState.update { it.copy(logoutError = throwable.message) }
                _logoutEvent.emit(Result.failure(throwable))
            }
            _uiState.update { it.copy(isLoggingOut = false) }
        }
    }
}