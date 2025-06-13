package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tech.kjo.kjo_mind_care.data.model.Category

class CategoryRepository {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        // En un entorno real, aquí usarías Firebase Firestore
        loadCategories()
    }

    private fun loadCategories() {
        // Simular un retraso de red
        // CoroutineScope().launch {
        // delay(1000)
        _categories.value = listOf(
            Category(
                id = "cat_anxiety",
                nameTranslations = mapOf("es" to "Ansiedad", "en" to "Anxiety"),
                isActive = true
            ),
            Category(
                id = "cat_stress",
                nameTranslations = mapOf("es" to "Estrés", "en" to "Stress"),
                isActive = true
            ),
            Category(
                id = "cat_meditation",
                nameTranslations = mapOf("es" to "Meditación", "en" to "Meditation"),
                isActive = true
            ),
            Category(
                id = "cat_nutrition",
                nameTranslations = mapOf("es" to "Nutrición", "en" to "Nutrition"),
                isActive = true
            ),
            Category(
                id = "cat_sleep",
                nameTranslations = mapOf("es" to "Sueño", "en" to "Sleep"),
                isActive = true
            ),
            Category(
                id = "cat_other",
                nameTranslations = mapOf("es" to "Otro", "en" to "Other"),
                isActive = true
            )
        )
        // }
    }

    suspend fun refreshCategories() {
        delay(1500) // Simula la recarga de red
        loadCategories()
    }
}