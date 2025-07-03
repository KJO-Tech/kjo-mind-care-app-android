package tech.kjo.kjo_mind_care.ui.main.mood

import android.util.Log
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.data.model.MoodType
import tech.kjo.kjo_mind_care.usecase.mood.GetMoodsUserUseCase
import tech.kjo.kjo_mind_care.usecase.mood.SaveMoodUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject
import tech.kjo.kjo_mind_care.R

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val getMoodsUserUseCase: GetMoodsUserUseCase,
    private val saveMoodUseCase: SaveMoodUseCase,
    private val currentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _historyUiState = MutableStateFlow(MoodHistoryUiState())
    val historyUiState: StateFlow<MoodHistoryUiState> = _historyUiState.asStateFlow()

    private val _saveMoodResult = MutableSharedFlow<Result<String>>(extraBufferCapacity = 1)
    val saveMoodResult: SharedFlow<Result<String>> = _saveMoodResult.asSharedFlow()


    init {
        collectMoodEntries()
    }

    private fun collectMoodEntries() {
        viewModelScope.launch {
            _historyUiState.update { it.copy(isLoading = true, error = null, moodEntries = emptyList()) }

            val currentUser = currentUserUseCase()
            val currentUserId = currentUser?.uid

            if (currentUserId == null) {
                val errorMessage = "Usuario no logueado. No se pueden obtener las entradas de ánimo."
                _historyUiState.update { it.copy(isLoading = false, error = errorMessage) }
                return@launch
            }

            try {
                getMoodsUserUseCase(currentUserId).collectLatest { moods ->

                    _historyUiState.update {
                        it.copy(
                            isLoading = false,
                            moodEntries = moods,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "Error al obtener entradas de ánimo: ${e.message}"
                _historyUiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }


    private fun mapMoodOptionToMoodType(moodOption: MoodOption): MoodType {
        return when (moodOption.titleRes) {
            R.string.mood_joyful_title -> MoodType.Joyful
            R.string.mood_neutral_title -> MoodType.Neutral
            R.string.mood_tired_title -> MoodType.Sad
            R.string.mood_sad_title -> MoodType.Sad
            R.string.mood_anxious_title -> MoodType.Anxious
            R.string.mood_angry_title -> MoodType.Sad
            R.string.mood_frustrated_title -> MoodType.Sad
            else -> MoodType.Neutral
        }
    }

    fun saveMood(moodOption: MoodOption, note: String) {
        viewModelScope.launch {
            _historyUiState.update { it.copy(isSaving = true) }

            val currentUser = currentUserUseCase()
            val currentUserId = currentUser?.uid


            if (currentUserId == null) {
                _historyUiState.update { it.copy(isSaving = false, error = "Usuario no logueado. No se puede guardar.") }
                _saveMoodResult.emit(Result.failure(IllegalStateException("Usuario no logueado.")))
                return@launch
            }

            val moodType = mapMoodOptionToMoodType(moodOption)

            val newMoodEntry = MoodEntry(
                mood = moodType,
                note = note,
                userId = currentUserId,
                createdAt = com.google.firebase.Timestamp.now()
            )

            val result = saveMoodUseCase(newMoodEntry)
            _saveMoodResult.emit(result)

            _historyUiState.update { it.copy(isSaving = false) }
        }
    }

    fun consumeSaveResult() {
        _saveMoodResult.tryEmit(Result.success("consumed"))
    }


    data class MoodHistoryUiState(
        val isLoading: Boolean = false,
        val moodEntries: List<MoodEntry> = emptyList(),
        val error: String? = null,
        val isSaving: Boolean = false
    )
}

