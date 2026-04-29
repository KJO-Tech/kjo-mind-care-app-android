package tech.kjo.kjo_mind_care.ui.main.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.usecase.user.UpdateUserUseCase
import javax.inject.Inject

data class EditProfileUiState(
    val name: String = "",
    val photoUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaveSuccess: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserDetails()
            if (user != null) {
                _uiState.update {
                    it.copy(
                        name = user.fullName,
                        photoUri = user.profileImage?.let { Uri.parse(it) }
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPhotoUriChange(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = updateUserUseCase(
                name = _uiState.value.name,
                photoUri = _uiState.value.photoUri
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.message) }
            }
        }
    }
}
