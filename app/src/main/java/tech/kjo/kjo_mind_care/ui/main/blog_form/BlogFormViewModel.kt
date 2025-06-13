package tech.kjo.kjo_mind_care.ui.main.blog_form

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.model.BlogPost
import tech.kjo.kjo_mind_care.data.model.BlogStatus
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.model.MediaType
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import tech.kjo.kjo_mind_care.data.repository.BlogRepository
import tech.kjo.kjo_mind_care.data.repository.CategoryRepository
import java.time.LocalDateTime

data class BlogFormUiState(
    val blogId: String? = null, // null para crear, ID para editar
    val title: String = "",
    val content: String = "",
    val selectedCategoryId: String? = null,
    val mediaUri: Uri? = null,
    val existingMediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val status: BlogStatus = BlogStatus.PUBLISHED,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val availableCategories: List<Category> = emptyList(),
    val showCategoryDialog: Boolean = false,

    // Validación
    val titleError: String? = null,
    val contentError: String? = null,
    val categoryError: String? = null
)

class BlogFormViewModel(
    private val savedStateHandle: SavedStateHandle, // Para el ID del blog si es edición
    private val blogRepository: BlogRepository = BlogRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlogFormUiState())
    val uiState: StateFlow<BlogFormUiState> = _uiState.asStateFlow()

    private val currentUserId = StaticBlogData.currentUser.uid

    init {
        // Cargar categorías disponibles
        viewModelScope.launch {
            categoryRepository.categories.collect { categories ->
                _uiState.update { it.copy(availableCategories = categories.filter { cat -> cat.isActive }) }
            }
        }

        // Cargar blog para edición si blogId existe en SavedStateHandle
        val blogId = savedStateHandle.get<String>("blogId")
        if (blogId != null) {
            _uiState.update { it.copy(blogId = blogId, isLoading = true) }
            viewModelScope.launch {
                val blog = blogRepository.getBlogById(blogId)
                if (blog != null) {
                    _uiState.update {
                        it.copy(
                            title = blog.title,
                            content = blog.content,
                            selectedCategoryId = blog.categoryId,
                            existingMediaUrl = blog.mediaUrl,
                            mediaType = blog.mediaType,
                            status = blog.status,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Blog no encontrado",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(content = newContent, contentError = null) }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                showCategoryDialog = false,
                categoryError = null
            )
        }
    }

    fun onMediaSelected(uri: Uri?, type: MediaType?) {
        _uiState.update {
            it.copy(
                mediaUri = uri,
                existingMediaUrl = null, // Si se selecciona un nuevo medio, borrar el existente
                mediaType = type
            )
        }
    }

    fun onClearMedia() {
        _uiState.update { it.copy(mediaUri = null, existingMediaUrl = null, mediaType = null) }
    }

    fun onStatusSelected(newStatus: BlogStatus) {
        _uiState.update { it.copy(status = newStatus) }
    }

    fun showCategorySelectionDialog(show: Boolean) {
        _uiState.update { it.copy(showCategoryDialog = show) }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(titleError = "El título no puede estar vacío") }
            isValid = false
        } else {
            _uiState.update { it.copy(titleError = null) }
        }

        if (_uiState.value.content.isBlank()) {
            _uiState.update { it.copy(contentError = "El contenido no puede estar vacío") }
            isValid = false
        } else {
            _uiState.update { it.copy(contentError = null) }
        }

        if (_uiState.value.selectedCategoryId == null) {
            _uiState.update { it.copy(categoryError = "Debe seleccionar una categoría") }
            isValid = false
        } else {
            _uiState.update { it.copy(categoryError = null) }
        }
        return isValid
    }

    fun saveBlog() {
        if (!validateForm()) {
            _uiState.update { it.copy(errorMessage = "Por favor, complete todos los campos requeridos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

            // Aquí iría la lógica de subida de medios a Cloudinary
            // Por simplicidad, si hay un nuevo mediaUri, lo usaremos como URL directamente
            // En un caso real, esto implicaría una función suspend para subir el archivo
            // y obtener la URL de descarga antes de guardar el BlogPost.
            val finalMediaUrl =
                _uiState.value.mediaUri?.toString() ?: _uiState.value.existingMediaUrl
            val finalMediaType = _uiState.value.mediaType
                ?: (if (finalMediaUrl != null) MediaType.IMAGE else null) // Asumir IMAGE si hay URL y no hay tipo

            val blog = BlogPost(
                id = _uiState.value.blogId
                    ?: "new_blog_id", // Se generará en el repositorio si es nuevo
                title = _uiState.value.title,
                content = _uiState.value.content,
                author = StaticBlogData.currentUser, // El autor es el usuario actual
                createdAt = LocalDateTime.now(), // Se sobrescribirá al crear/actualizar en el repo simulado
                mediaUrl = finalMediaUrl,
                mediaType = finalMediaType,
                likes = 0,
                comments = 0,
                isLiked = false,
                categoryId = _uiState.value.selectedCategoryId,
                status = _uiState.value.status
            )

            val result = if (_uiState.value.blogId == null) {
                blogRepository.createBlog(blog)
            } else {
                blogRepository.updateBlog(blog)
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            errorMessage = null // Limpiar cualquier error previo
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = false,
                            errorMessage = "Error al guardar el blog: ${e.localizedMessage ?: "Desconocido"}"
                        )
                    }
                }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}