package tech.kjo.kjo_mind_care.ui.main.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.ActivityCategory
import tech.kjo.kjo_mind_care.data.model.DailyExercise
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.data.repository.IDailyActivityRepository
import tech.kjo.kjo_mind_care.data.repository.impl.DailyActivityRepository
import tech.kjo.kjo_mind_care.utils.Resource
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val selectedDailyActivities: List<DailyActivityWithCategory> = emptyList(),
    val isLoadingActivities: Boolean = true,
    val activitiesError: String? = null,
    val userName: String = "User"
)

data class DailyActivityWithCategory(
    val exercise: DailyExercise,
    val category: ActivityCategory
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyActivityRepository: IDailyActivityRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var lastActivitySelectionDate: LocalDate? = null
    private var cachedSelectedActivities: List<DailyActivityWithCategory>? = null

    init {
        fetchUserName()
        viewModelScope.launch {
            fetchDailyActivities()
        }
    }

    private fun fetchUserName() {
        viewModelScope.launch {
            val userUid = authRepository.getCurrentUserUid()
            if (userUid != null) {
                val userDetails = authRepository.getUserDetails(userUid)
                _uiState.value = _uiState.value.copy(
                    userName = userDetails?.fullName?.split(" ")?.firstOrNull() ?: "User"
                )
            }
        }
    }

    fun fetchDailyActivities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingActivities = true, activitiesError = null)

            val currentDate = LocalDate.now()

            // Si ya seleccionamos actividades hoy, las devolvemos de la caché
            if (currentDate == lastActivitySelectionDate && !cachedSelectedActivities.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    selectedDailyActivities = cachedSelectedActivities!!,
                    isLoadingActivities = false
                )
                return@launch
            }

            // Si no, o si es un nuevo día, generamos nuevas actividades
            try {
                val categoriesResource =
                    dailyActivityRepository.getCategories().first()
                Log.d("HomeViewModel", "Categories: $categoriesResource")
                if (categoriesResource is Resource.Error) {
                    _uiState.value = _uiState.value.copy(
                        activitiesError = categoriesResource.message,
                        isLoadingActivities = false
                    )
                    return@launch
                }
                val allCategories = categoriesResource.data ?: emptyList()

                val selectedExercisesWithCategories = mutableListOf<DailyActivityWithCategory>()
                val usedCategoryIds = mutableSetOf<String>()

                val availableCategories = allCategories.shuffled() // Aleatorio

                for (category in availableCategories) {
                    if (selectedExercisesWithCategories.size >= 3) break

                    val exercisesResource =
                        dailyActivityRepository.getExercisesByCategory(category.id).first()
                    if (exercisesResource is Resource.Success && !exercisesResource.data.isNullOrEmpty()) {
                        val availableExercises =
                            exercisesResource.data.filter { it.categoryId == category.id }
                        if (availableExercises.isNotEmpty()) {
                            val selectedExercise =
                                availableExercises.random()
                            selectedExercisesWithCategories.add(
                                DailyActivityWithCategory(
                                    selectedExercise,
                                    category
                                )
                            )
                            usedCategoryIds.add(category.id)
                        }
                    }
                }

                lastActivitySelectionDate = currentDate
                cachedSelectedActivities = selectedExercisesWithCategories
                _uiState.value = _uiState.value.copy(
                    selectedDailyActivities = selectedExercisesWithCategories,
                    isLoadingActivities = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    activitiesError = e.localizedMessage ?: "Error fetching daily activities",
                    isLoadingActivities = false
                )
                e.printStackTrace()
            }
        }
    }
}