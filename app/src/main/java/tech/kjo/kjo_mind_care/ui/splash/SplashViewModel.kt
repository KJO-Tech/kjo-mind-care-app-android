package tech.kjo.kjo_mind_care.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import tech.kjo.kjo_mind_care.utils.AndroidNetworkMonitor
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val networkMonitor: AndroidNetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading
            delay(1000L)

            // Verificar si hay conexión a internet
            if (!networkMonitor.isOnline()) {
                _uiState.value = SplashUiState.NetworkError
                return@launch
            }

            try {
                val currentUserUid = getCurrentUserUseCase()
                if (currentUserUid != null) {
                    _uiState.value = SplashUiState.Authenticated
                } else {
                    _uiState.value = SplashUiState.Unauthenticated
                }
            } catch (e: Exception) {
                // Manejo específico de errores de red
                if (e is IOException || e is SocketTimeoutException) {
                    _uiState.value = SplashUiState.NetworkError
                } else {
                    _uiState.value = SplashUiState.GeneralError
                    e.printStackTrace()
                }
            }
        }
    }
}