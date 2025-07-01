package tech.kjo.kjo_mind_care.ui.main.daily_exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.DailyExercise
import tech.kjo.kjo_mind_care.data.repository.IDailyActivityRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

@HiltViewModel
class DailyExerciseDetailViewModel @Inject constructor(
    private val dailyActivityRepository: IDailyActivityRepository
) : ViewModel() {

    private val _exerciseState = MutableStateFlow<Resource<DailyExercise?>>(Resource.Loading())
    val exerciseState = _exerciseState.asStateFlow()

    fun loadExercise(exerciseId: String) {
        viewModelScope.launch {
            dailyActivityRepository.getExerciseById(exerciseId).collect { resource ->
                _exerciseState.value = resource
            }
        }
    }
}