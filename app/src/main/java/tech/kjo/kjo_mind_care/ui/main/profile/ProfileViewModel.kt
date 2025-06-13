package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            photoUrl = "https://avatars.githubusercontent.com/u/102635058?v=4",
            name = "Ryan",
            email = "ryan@gmail.com",
            checkIns = 28,
            posts = 12,
            badges = 3,
            darkMode = true
        )
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

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
}