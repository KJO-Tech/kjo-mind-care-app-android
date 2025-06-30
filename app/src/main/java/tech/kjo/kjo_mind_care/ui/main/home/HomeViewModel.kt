package tech.kjo.kjo_mind_care.ui.main.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.ActivityCategory
import tech.kjo.kjo_mind_care.data.model.DailyExercise
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.data.repository.IDailyActivityRepository
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
        fetchDailyActivities()
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

            // Si ya seleccionamos actividades hoy Y la caché no está vacía, las devolvemos.
            if (currentDate == lastActivitySelectionDate && !cachedSelectedActivities.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    selectedDailyActivities = cachedSelectedActivities!!,
                    isLoadingActivities = false
                )
                return@launch
            }

            var allCategories: List<ActivityCategory> = emptyList()
            var categoriesFetchError: String? = null

            val categoriesJob = launch {
                dailyActivityRepository.getCategories().collect { categoriesResource ->
                    when (categoriesResource) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            allCategories = categoriesResource.data ?: emptyList()
                            this.cancel()
                        }

                        is Resource.Error -> {
                            categoriesFetchError = categoriesResource.message
                            _uiState.value = _uiState.value.copy(
                                activitiesError = categoriesFetchError,
                                isLoadingActivities = false
                            )
                            this.cancel()
                        }
                    }
                }
            }
            categoriesJob.join()

            if (categoriesFetchError != null) {
                return@launch
            }

            if (allCategories.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    selectedDailyActivities = emptyList(),
                    isLoadingActivities = false,
                )
                return@launch
            }

            val selectedExercisesWithCategories = mutableListOf<DailyActivityWithCategory>()

            val availableCategories = allCategories.shuffled() // Aleatorio

            for (category in availableCategories) {
                if (selectedExercisesWithCategories.size >= 3) break

                var currentExercises: List<DailyExercise>? = null
                var exercisesFetchErrorForCategory: String? = null

                val exercisesJob = launch {
                    dailyActivityRepository.getExercisesByCategory(category.id)
                        .collect { exercisesResource ->
                            when (exercisesResource) {
                                is Resource.Loading -> {
                                }

                                is Resource.Success -> {
                                    currentExercises = exercisesResource.data ?: emptyList()
                                    this.cancel()
                                }

                                is Resource.Error -> {
                                    exercisesFetchErrorForCategory = exercisesResource.message
                                    this.cancel()
                                }
                            }
                        }
                }
                exercisesJob.join()

                if (exercisesFetchErrorForCategory != null) {
                    continue
                }

                if (!currentExercises.isNullOrEmpty()) {
                    val availableExercises =
                        currentExercises!!.filter { it.categoryId == category.id }
                    if (availableExercises.isNotEmpty()) {
                        val selectedExercise = availableExercises.random()
                        selectedExercisesWithCategories.add(
                            DailyActivityWithCategory(
                                selectedExercise,
                                category
                            )
                        )
                    } else {
                        Log.d(
                            "HomeViewModel",
                            "No hay ejercicios disponibles para la categoría: ${category.localizedName["en"]} después de filtrar."
                        )
                    }
                } else {
                    Log.d(
                        "HomeViewModel",
                        "No se encontraron ejercicios (o lista vacía) para la categoría: ${category.localizedName["en"]}"
                    )
                }
            }

            if (selectedExercisesWithCategories.isNotEmpty()) {
                lastActivitySelectionDate = currentDate
                cachedSelectedActivities = selectedExercisesWithCategories
                _uiState.value = _uiState.value.copy(
                    selectedDailyActivities = selectedExercisesWithCategories,
                    isLoadingActivities = false,
                    activitiesError = null
                )
            } else {
                Log.w(
                    "HomeViewModel",
                    "No se pudieron seleccionar actividades diarias para hoy. Caché no actualizada."
                )
                _uiState.value = _uiState.value.copy(
                    selectedDailyActivities = emptyList(),
                    isLoadingActivities = false,
                )
            }
        }
    }
}