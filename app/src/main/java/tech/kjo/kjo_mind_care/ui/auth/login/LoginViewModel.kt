package tech.kjo.kjo_mind_care.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.usecase.user.LoginUseCase
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<String>?>(null)
    val loginState: StateFlow<Resource<String>?> = _loginState

    fun login(email: String, password: String) {
        try {
            if (email.isBlank() || password.isBlank()) {
                _loginState.value = Resource.Error("Todos los campos son obligatorios")
                return
            }
            viewModelScope.launch {
                _loginState.value = Resource.Loading()
                _loginState.value = loginUseCase(email, password)
            }
        } catch (e: Exception) {
            _loginState.value = Resource.Error(e.message ?: "Error al iniciar sesión", null)
        }
    }

    fun resetState() {
        _loginState.value = null
    }

}