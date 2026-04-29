package tech.kjo.kjo_mind_care.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.usecase.user.LoginUseCase
import tech.kjo.kjo_mind_care.usecase.user.SignInWithGoogleUseCase
import tech.kjo.kjo_mind_care.usecase.user.UpdateUserTokenUseCase
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val updateUserTokenUseCase: UpdateUserTokenUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<String>?>(null)
    val loginState: StateFlow<Resource<String>?> = _loginState

    private val _googleSignInState = MutableStateFlow<Resource<Boolean>?>(null)
    val googleSignInState: StateFlow<Resource<Boolean>?> = _googleSignInState

    fun login(email: String, password: String) {
        try {
            if (email.isBlank() || password.isBlank()) {
                _loginState.value = Resource.Error("Todos los campos son obligatorios")
                return
            }
            viewModelScope.launch {
                _loginState.value = Resource.Loading()
                val result = loginUseCase(email, password)
                if (result is Resource.Success) {
                    updateUserTokenUseCase()
                }
                _loginState.value = result
            }
        } catch (e: Exception) {
            _loginState.value = Resource.Error(e.message ?: "Error al iniciar sesión", null)
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "signInWithGoogle llamado. Iniciando carga...")
            _googleSignInState.value = Resource.Loading()
            val result = signInWithGoogleUseCase(account)
            Log.d("LoginViewModel", "Resultado de signInWithGoogleUseCase: $result")
            if (result is Resource.Success) {
                updateUserTokenUseCase()
            }
            _googleSignInState.value = result
        }
    }

    fun resetState() {
        _loginState.value = null
        _googleSignInState.value = null
    }

}