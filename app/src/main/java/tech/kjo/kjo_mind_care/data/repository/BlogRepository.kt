package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.delay
import tech.kjo.kjo_mind_care.data.model.BlogPost
import tech.kjo.kjo_mind_care.data.model.BlogStatus
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Este es un repositorio simulado. En una app real, interactuarías con Firestore.
class BlogRepository {

    // Usamos MutableStateFlow para simular una base de datos que se actualiza
    // y para que los ViewModels puedan observar los cambios.
    private val _blogPosts = MutableStateFlow(StaticBlogData.getSampleBlogPosts().toMutableList())
    val blogPosts = _blogPosts.asStateFlow()

    init {
        // Aquí podríamos cargar los datos desde Firestore si fuera real.
    }

    suspend fun getBlogById(blogId: String): BlogPost? {
        delay(500) // Simular retardo de red
        return _blogPosts.value.find { it.id == blogId }
    }

    suspend fun createBlog(blogPost: BlogPost): Result<String> {
        delay(1000) // Simular retardo de red para creación
        val newId = "blog_${UUID.randomUUID().toString().take(8)}"
        val blogWithId = blogPost.copy(id = newId, createdAt = LocalDateTime.now())
        _blogPosts.update { currentList ->
            currentList.apply { add(0, blogWithId) } // Añadir al principio para que se vea
        }
        return Result.success(newId)
    }

    suspend fun updateBlog(updatedBlog: BlogPost): Result<Unit> {
        delay(1000) // Simular retardo de red para actualización
        val index = _blogPosts.value.indexOfFirst { it.id == updatedBlog.id }
        if (index != -1) {
            _blogPosts.update { currentList ->
                currentList.apply { set(index, updatedBlog.copy(createdAt = LocalDateTime.now())) } // Actualizar y poner fecha de modificación
            }
            return Result.success(Unit)
        }
        return Result.failure(Exception("Blog not found"))
    }

    // Funciones para manejar estados (opcional, podrías hacerlo vía updateBlog)
    suspend fun deleteBlog(blogId: String): Result<Unit> {
        delay(500)
        val index = _blogPosts.value.indexOfFirst { it.id == blogId }
        if (index != -1) {
            _blogPosts.update { currentList ->
                currentList.apply { set(index, get(index).copy(status = BlogStatus.DELETED)) }
            }
            return Result.success(Unit)
        }
        return Result.failure(Exception("Blog not found for deletion"))
    }

    suspend fun publishBlog(blogId: String): Result<Unit> {
        delay(500)
        val index = _blogPosts.value.indexOfFirst { it.id == blogId }
        if (index != -1) {
            _blogPosts.update { currentList ->
                currentList.apply { set(index, get(index).copy(status = BlogStatus.PUBLISHED)) }
            }
            return Result.success(Unit)
        }
        return Result.failure(Exception("Blog not found for publishing"))
    }
}