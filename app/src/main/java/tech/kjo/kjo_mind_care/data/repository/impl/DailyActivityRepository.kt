package tech.kjo.kjo_mind_care.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.ActivityCategory
import tech.kjo.kjo_mind_care.data.model.DailyExercise
import tech.kjo.kjo_mind_care.data.repository.IDailyActivityRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class DailyActivityRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IDailyActivityRepository {

    override fun getCategories(): Flow<Resource<List<ActivityCategory>>> = flow {
        emit(Resource.Loading())
        try {
            val snapshot = firestore.collection("activityCategories")
                .orderBy("order")
                .get()
                .await()
            val categories = snapshot.toObjects(ActivityCategory::class.java)
            emit(Resource.Success(categories))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error fetching categories"))
        }
    }

    override fun getExercisesByCategory(categoryId: String): Flow<Resource<List<DailyExercise>>> =
        flow {
            emit(Resource.Loading())
            try {
                val snapshot = firestore.collection("dailyExercises")
                    .whereEqualTo("categoryId", categoryId)
                    .get()
                    .await()
                val exercises = snapshot.toObjects(DailyExercise::class.java)
                emit(Resource.Success(exercises))
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Unknown error fetching exercises"))
            }
        }

    override fun getExerciseById(exerciseId: String): Flow<Resource<DailyExercise>> = flow {
        emit(Resource.Loading())
        try {
            val document = firestore.collection("dailyExercises").document(exerciseId).get().await()
            val exercise = document.toObject(DailyExercise::class.java)
            if (exercise != null) {
                emit(Resource.Success(exercise))
            } else {
                emit(Resource.Error("Exercise not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error fetching exercise"))
        }
    }
}