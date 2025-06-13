package tech.kjo.kjo_mind_care.ui.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.usecase.user.RegisterUseCase
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FullNameChanged -> {
                state = state.copy(fullName = event.value)
            }
            is RegisterEvent.EmailChanged -> {
                state = state.copy(email = event.value)
            }
            is RegisterEvent.PasswordChanged -> {
                state = state.copy(password = event.value)
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                state = state.copy(confirmPassword = event.value)
            }
            RegisterEvent.Submit -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            if (state.password != state.confirmPassword) {
                state = state.copy(
                    isLoading = false,
                    error = "Las contraseñas no coinciden"
                )
                return@launch
            }

            if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
                state = state.copy(
                    isLoading = false,
                    error = "Todos los campos son obligatorios"
                )
                return@launch
            }

            val result = registerUseCase(
                fullName = state.fullName,
                email = state.email,
                password = state.password
            )

            state = when (result) {
                is Resource.Success -> {
                    state.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is Resource.Error -> {
                    state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading<*> -> TODO()
            }
        }
    }
}

data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed class RegisterEvent {
    data class FullNameChanged(val value: String) : RegisterEvent()
    data class EmailChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent()
    object Submit : RegisterEvent()
}