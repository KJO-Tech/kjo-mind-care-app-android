package tech.kjo.kjo_mind_care.data.repository

import tech.kjo.kjo_mind_care.data.model.ActivityCategory
import tech.kjo.kjo_mind_care.data.model.DailyExercise
import tech.kjo.kjo_mind_care.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IDailyActivityRepository {
    fun getCategories(): Flow<Resource<List<ActivityCategory>>>
    fun getExercisesByCategory(categoryId: String): Flow<Resource<List<DailyExercise>>>
    fun getExerciseById(exerciseId: String): Flow<Resource<DailyExercise>>
}