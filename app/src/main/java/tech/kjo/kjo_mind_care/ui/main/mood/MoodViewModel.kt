package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.Mood
import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.usecase.mood.GetMoodEntriesUseCase
import tech.kjo.kjo_mind_care.usecase.mood.GetMoodsUseCase
import tech.kjo.kjo_mind_care.usecase.mood.SaveMoodUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val getMoodsUseCase: GetMoodsUseCase,
    private val saveMoodUseCase: SaveMoodUseCase,
    private val currentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _historyUiState = MutableStateFlow(MoodHistoryUiState())
    val historyUiState: StateFlow<MoodHistoryUiState> = _historyUiState.asStateFlow()

    private val _moodsUiState = MutableStateFlow(MoodsUiState())
    val moodsUiState: StateFlow<MoodsUiState> = _moodsUiState.asStateFlow()

    private val _saveMoodResult = MutableSharedFlow<Result<String>>(extraBufferCapacity = 1)
    val saveMoodResult: SharedFlow<Result<String>> = _saveMoodResult.asSharedFlow()

    init {
        collectMoodData()
    }

    private fun collectMoodData() {
        viewModelScope.launch {
            _historyUiState.update { it.copy(isLoading = true, error = null) }
            val currentUser = currentUserUseCase()
            val currentUserId = currentUser?.uid

            if (currentUserId == null) {
                _historyUiState.update { it.copy(isLoading = false, error = "User not logged in.") }
                return@launch
            }

            try {
                combine(
                    getMoodEntriesUseCase(currentUserId),
                    getMoodsUseCase()
                ) { moodEntries, moods ->
                    val moodsMap = moods.associateBy { it.id }
                    _historyUiState.update {
                        it.copy(
                            isLoading = false,
                            moodEntries = moodEntries,
                            moodsMap = moodsMap
                        )
                    }
                    _moodsUiState.update {
                        it.copy(
                            isLoading = false,
                            moods = moods
                        )
                    }
                }.collectLatest { }
            } catch (e: Exception) {
                _historyUiState.update { it.copy(isLoading = false, error = e.message) }
                _moodsUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onMoodSelected(mood: Mood) {
        _moodsUiState.update { it.copy(selectedMood = mood) }
    }

    fun saveMood(moodId: String, note: String) {
        viewModelScope.launch {
            _historyUiState.update { it.copy(isSaving = true) }

            val currentUser = currentUserUseCase()
            val currentUserId = currentUser?.uid

            if (currentUserId == null) {
                _historyUiState.update { it.copy(isSaving = false, error = "User not logged in.") }
                _saveMoodResult.tryEmit(Result.failure(IllegalStateException("User not logged in.")))
                return@launch
            }

            val newMoodEntry = MoodEntry(
                moodId = moodId,
                note = note,
                userId = currentUserId
            )

            val result = saveMoodUseCase(newMoodEntry)
            _saveMoodResult.tryEmit(result)

            _historyUiState.update { it.copy(isSaving = false) }
        }
    }

    data class MoodHistoryUiState(
        val isLoading: Boolean = false,
        val moodEntries: List<MoodEntry> = emptyList(),
        val moodsMap: Map<String, Mood> = emptyMap(),
        val error: String? = null,
        val isSaving: Boolean = false
    )

    data class MoodsUiState(
        val isLoading: Boolean = false,
        val moods: List<Mood> = emptyList(),
        val selectedMood: Mood? = null,
        val error: String? = null
    )
}
