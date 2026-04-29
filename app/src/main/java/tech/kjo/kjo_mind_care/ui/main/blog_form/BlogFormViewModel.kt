package tech.kjo.kjo_mind_care.ui.main.blog_form

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.enums.MediaType
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import tech.kjo.kjo_mind_care.data.model.StaticBlogData.init
import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.impl.BlogRepository
import tech.kjo.kjo_mind_care.data.repository.impl.CategoryRepository
import tech.kjo.kjo_mind_care.usecase.blog.CreateBlogUseCase
import tech.kjo.kjo_mind_care.usecase.blog.GetBlogByIdUseCase
import tech.kjo.kjo_mind_care.usecase.blog.UpdateBlogUseCase
import tech.kjo.kjo_mind_care.usecase.blog.UploadMediaUseCase
import tech.kjo.kjo_mind_care.usecase.category.GetCategoriesUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import java.time.LocalDateTime
import javax.inject.Inject

data class BlogFormUiState(
    val blogId: String? = null,
    val title: String = "",
    val content: String = "",
    val selectedCategoryId: String? = null,
    val availableCategories: List<Category> = emptyList(),
    val mediaUri: Uri? = null,
    val existingMediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val status: BlogStatus = BlogStatus.PENDING,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val titleError: String? = null,
    val contentError: String? = null,
    val categoryError: String? = null,
    val showCategoryDialog: Boolean = false,
    val currentUser: User? = null
)

@HiltViewModel
class BlogFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val createBlogPostUseCase: CreateBlogUseCase,
    private val getBlogPostByIdUseCase: GetBlogByIdUseCase,
    private val updateBlogPostUseCase: UpdateBlogUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val uploadMediaUseCase: UploadMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlogFormUiState())
    val uiState: StateFlow<BlogFormUiState> = _uiState.asStateFlow()

    private val currentUserId = StaticBlogData.currentUser.uid

    init {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _uiState.update { it.copy(currentUser = user) }
            if (user == null) {
                _uiState.update { it.copy(errorMessage = "Usuario no autenticado. Por favor, inicie sesión.") }
            }
        }

        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(availableCategories = categories.filter { cat -> cat.isActive }) }
            }
        }

        val blogId = savedStateHandle.get<String>("blogId")
        if (blogId != null) {
            _uiState.update { it.copy(blogId = blogId, isLoading = true) }
            viewModelScope.launch {
                val blog = getBlogPostByIdUseCase(blogId)
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
                existingMediaUrl = null,
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

        val currentUser = _uiState.value.currentUser ?: run {
            _uiState.update { it.copy(errorMessage = "No se pudo guardar el blog: Usuario no autenticado.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

            var finalMediaUrl: String? = _uiState.value.existingMediaUrl
            var finalMediaType: MediaType? = _uiState.value.mediaType

            val mediaUriToUpload = _uiState.value.mediaUri
            val selectedMediaType = _uiState.value.mediaType

            if (mediaUriToUpload != null && selectedMediaType != null) {
                val uploadResult = uploadMediaUseCase(mediaUriToUpload, selectedMediaType)
                uploadResult
                    .onSuccess { uploadedUrl ->
                        finalMediaUrl = uploadedUrl
                        finalMediaType = selectedMediaType
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveSuccess = false,
                                errorMessage = "Error al subir el medio: ${e.localizedMessage ?: "Desconocido"}"
                            )
                        }
                        return@launch
                    }
            } else if (mediaUriToUpload == null && _uiState.value.existingMediaUrl != null && _uiState.value.mediaType != null) {

            } else {
                finalMediaUrl = null
                finalMediaType = null
            }


            val blog = Blog(
                id = _uiState.value.blogId ?: "",
                title = _uiState.value.title,
                content = _uiState.value.content,
                author = currentUser,
                createdAt = Timestamp.now(),
                mediaUrl = finalMediaUrl,
                mediaType = finalMediaType,
                likes = 0,
                comments = 0,
                isLiked = false,
                categoryId = _uiState.value.selectedCategoryId,
                status = _uiState.value.status
            )

            val result = if (_uiState.value.blogId == null) {
                createBlogPostUseCase(blog)
            } else {
                updateBlogPostUseCase(blog)
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            errorMessage = null,
                            mediaUri = null,
                            existingMediaUrl = finalMediaUrl,
                            mediaType = finalMediaType
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