package tech.kjo.kjo_mind_care.data.repository.impl


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.repository.ICategoryRepository
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
): ICategoryRepository {
    private val categoriesCollection = firestore.collection("categories")

    override fun getCategories(): Flow<List<Category>> = callbackFlow {
        val subscription = categoriesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val categories = snapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(Category::class.java)?.copy(id = document.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(categories).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getCategoryById(categoryId: String): Category? {
        return try {
            val document = categoriesCollection.document(categoryId).get().await()
            document.toObject(Category::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createCategory(category: Category): Result<String> {
        return try {
            val docRef = categoriesCollection.add(category).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            categoriesCollection.document(categoryId).update("isActive", false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}