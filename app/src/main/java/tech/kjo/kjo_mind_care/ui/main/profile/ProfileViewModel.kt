package tech.kjo.kjo_mind_care.ui.main.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.service.MyFirebaseMessagingService
import tech.kjo.kjo_mind_care.service.NotificationScheduler
import tech.kjo.kjo_mind_care.usecase.blog.GetUserPostsCountUseCase
import tech.kjo.kjo_mind_care.usecase.mood.GetMoodEntriesCountUseCase
import tech.kjo.kjo_mind_care.usecase.user.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: IAuthRepository,
    private val getMoodEntriesCountUseCase: GetMoodEntriesCountUseCase,
    private val getUserPostsCountUseCase: GetUserPostsCountUseCase,
    private val sharedPreferences: SharedPreferences,
    private val notificationScheduler: NotificationScheduler
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _logoutEvent = kotlinx.coroutines.flow.MutableSharedFlow<Result<Unit>>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Result<Unit>> = _logoutEvent.asSharedFlow()

    init {
        loadSettings()
        refreshUserDetails()
    }

    fun refreshUserDetails() {
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
                            _uiState.value.copy(
                                name = currentUserObject?.fullName ?: "–",
                                email = currentUserObject?.email ?: "–",
                                photoUrl = currentUserObject?.profileImage ?: "",
                                checkIns = moodEntriesCount.toInt(),
                                posts = postsCount.toInt(),
                                badges = 0,
                            )
                        }
                    } else {
                        flowOf(ProfileUiState())
                    }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    private fun loadSettings() {
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        val reminderHour = sharedPreferences.getInt("reminder_hour", 20)
        val reminderMinute = sharedPreferences.getInt("reminder_minute", 0)
        val darkMode = sharedPreferences.getBoolean("dark_mode", true)
        _uiState.update {
            it.copy(
                notifications = notificationsEnabled,
                reminderTime = Pair(reminderHour, reminderMinute),
                darkMode = darkMode
            )
        }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        sharedPreferences.edit().putInt("reminder_hour", hour).apply()
        sharedPreferences.edit().putInt("reminder_minute", minute).apply()
        _uiState.update { it.copy(reminderTime = Pair(hour, minute)) }
        if (_uiState.value.notifications) {
            notificationScheduler.scheduleDailyMoodReminder(hour, minute)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
        _uiState.update { it.copy(notifications = enabled) }
        if (enabled) {
            val (hour, minute) = _uiState.value.reminderTime
            notificationScheduler.scheduleDailyMoodReminder(hour, minute)
        } else {
            notificationScheduler.cancelDailyMoodReminder()
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
        _uiState.update { it.copy(darkMode = enabled) }
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return

        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                MyFirebaseMessagingService().onUserLogout(userId, token)
            }
        }

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
