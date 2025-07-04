package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.usecase.blog.GetUserPostsCountUseCase
import tech.kjo.kjo_mind_care.usecase.mood.GetMoodEntriesCountUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import tech.kjo.kjo_mind_care.usecase.user.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: IAuthRepository,
    private val getMoodEntriesCountUseCase: GetMoodEntriesCountUseCase,
    private val getUserPostsCountUseCase: GetUserPostsCountUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            photoUrl = "https://avatars.githubusercontent.com/u/102635058?v=4",
            name = "Ryan",
            email = "ryan@gmail.com",
            checkIns = 0,
            posts = 0,
            badges = 0,
            darkMode = true,
            isLoggingOut = false,
            logoutError = null
        )
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Result<Unit>>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Result<Unit>> = _logoutEvent.asSharedFlow()


    init {
        viewModelScope.launch {
            authRepository.observeCurrentUser()
                .flatMapLatest { user ->
                    val userId = user?.uid
                    if (userId != null) {

                        combine(
                            flowOf(user),
                            getMoodEntriesCountUseCase(userId),
                            getUserPostsCountUseCase(userId)
                        ) { currentUserObject, moodEntriesCount, postsCount ->
                            ProfileUiState(
                                name = currentUserObject?.fullName ?: "–",
                                email = currentUserObject?.email ?: "–",
                                photoUrl = currentUserObject?.profileImage ?: "",
                                checkIns = moodEntriesCount.toInt(),
                                posts = postsCount.toInt(),
                                badges = 0,
                            )
                        }
                    } else {
                        flowOf(
                            ProfileUiState(
                                photoUrl = "",
                                name = "Not Logged In",
                                email = "",
                                checkIns = 0,
                                posts = 0,
                                badges = 0,
                            )
                        )
                    }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

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