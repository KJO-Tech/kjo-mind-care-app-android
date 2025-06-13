package tech.kjo.kjo_mind_care.ui.main.blog_detail

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
import tech.kjo.kjo_mind_care.data.model.Comment
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import tech.kjo.kjo_mind_care.data.repository.BlogRepository
import java.time.LocalDateTime
import java.util.UUID

data class BlogDetailUiState(
    val blogPost: BlogPost? = null,
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCommentInput: Boolean = false,
    val commentToReplyTo: String? = null,
    val commentToEdit: String? = null,
    val currentCommentText: String = "",
    val isSendingComment: Boolean = false,
    val isDeletingBlog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val deleteSuccess: Boolean = false,
    val blogStatusMessage: String? = null
)

class BlogDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val blogRepository: BlogRepository = BlogRepository()
) : ViewModel() {

    private val blogId: String = checkNotNull(savedStateHandle["blogId"])

    private val _uiState = MutableStateFlow(BlogDetailUiState())
    val uiState: StateFlow<BlogDetailUiState> = _uiState.asStateFlow()

    private val currentUser = StaticBlogData.currentUser // Asume un usuario logueado

    init {
        // Cargar blog al inicializar el ViewModel
        loadBlogDetail()

        // Observar cambios en los blogs del repositorio por si se actualiza desde otro lugar (ej. formulario de edición)
        viewModelScope.launch {
            blogRepository.blogPosts.collect { blogs ->
                val updatedBlog = blogs.find { it.id == blogId }
                _uiState.update { currentState ->
                    currentState.copy(
                        blogPost = updatedBlog,
                        blogStatusMessage = updatedBlog?.let {
                            when (it.status) {
                                BlogStatus.DELETED -> "Este blog ha sido eliminado."
                                BlogStatus.PENDING -> "Este blog está pendiente de publicación."
                                else -> null
                            }
                        }
                    )
                }
            }
        }
    }

    private fun loadBlogDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Usar el repositorio para obtener el blog
                val blog = blogRepository.getBlogById(blogId)
                val comments = StaticBlogData.getSampleCommentsForBlog(blogId)
                _uiState.update {
                    it.copy(
                        blogPost = blog,
                        comments = comments,
                        isLoading = false,
                        blogStatusMessage = blog?.let {
                            when (it.status) {
                                BlogStatus.DELETED -> "Este blog ha sido eliminado."
                                BlogStatus.PENDING -> "Este blog está pendiente de publicación."
                                else -> null
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar el blog: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleLike() {
        _uiState.update { current ->
            current.blogPost?.let { blog ->
                current.copy(
                    blogPost = blog.copy(
                        isLiked = !blog.isLiked,
                        likes = if (blog.isLiked) blog.likes - 1 else blog.likes + 1
                    )
                )
            } ?: current
        }
        // En una app real: actualizar el like en el repositorio/backend
    }

    fun isCurrentUserAuthor(): Boolean {
        return uiState.value.blogPost?.author?.uid == currentUser.uid
    }

    fun showDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }

    fun deleteBlog() {
        val blogToDeleteId = uiState.value.blogPost?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingBlog = true, error = null) }
            val result = blogRepository.deleteBlog(blogToDeleteId)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingBlog = false,
                            deleteSuccess = true,
                            showDeleteDialog = false,
                            blogStatusMessage = "Este blog ha sido eliminado." // Actualizar mensaje de estado
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isDeletingBlog = false,
                            showDeleteDialog = false,
                            error = "Error al eliminar el blog: ${e.localizedMessage}"
                        )
                    }
                }
        }
    }

    // --- Funciones para manejar el input de comentarios ---
    fun onAddCommentClick() {
        _uiState.update {
            it.copy(
                showCommentInput = true,
                commentToReplyTo = null,
                commentToEdit = null,
                currentCommentText = ""
            )
        }
    }

    fun onReplyToComment(commentId: String) {
        _uiState.update {
            it.copy(
                showCommentInput = true,
                commentToReplyTo = commentId,
                commentToEdit = null,
                currentCommentText = ""
            )
        }
    }

    fun onEditComment(commentId: String, currentContent: String) {
        _uiState.update {
            it.copy(
                showCommentInput = true,
                commentToEdit = commentId,
                commentToReplyTo = null,
                currentCommentText = currentContent
            )
        }
    }

    fun onCancelCommentInput() {
        _uiState.update {
            it.copy(
                showCommentInput = false,
                commentToReplyTo = null,
                commentToEdit = null,
                currentCommentText = ""
            )
        }
    }

    fun onCommentTextChanged(newText: String) {
        _uiState.update { it.copy(currentCommentText = newText) }
    }

    fun onSaveComment() {
        viewModelScope.launch {
            val currentText = _uiState.value.currentCommentText
            if (currentText.isBlank()) {
                _uiState.update { it.copy(error = "El comentario no puede estar vacío.") }
                return@launch
            }
            _uiState.update { it.copy(isSendingComment = true) }

            if (_uiState.value.commentToEdit != null) {
                val commentIdToEdit = _uiState.value.commentToEdit!!
                _uiState.update { currentState ->
                    currentState.copy(
                        comments = updateCommentContentRecursive(
                            currentState.comments,
                            commentIdToEdit,
                            currentText
                        )
                    )
                }
            } else {
                val newComment = Comment(
                    id = UUID.randomUUID().toString(),
                    author = currentUser,
                    content = currentText,
                    createdAt = LocalDateTime.now(),
                    isMine = true
                )

                _uiState.update { currentState ->
                    val updatedComments = if (_uiState.value.commentToReplyTo != null) {
                        addReplyToCommentRecursive(
                            currentState.comments,
                            _uiState.value.commentToReplyTo!!,
                            newComment
                        )
                    } else {
                        currentState.comments + newComment
                    }
                    currentState.copy(comments = updatedComments)
                }
            }
            onCancelCommentInput()
            _uiState.update { it.copy(isSendingComment = false, error = null) }
        }
    }

    private fun addReplyToCommentRecursive(
        comments: List<Comment>,
        parentId: String,
        newReply: Comment
    ): List<Comment> {
        return comments.map { comment ->
            if (comment.id == parentId) {
                comment.copy(replies = comment.replies + newReply)
            } else {
                comment.copy(
                    replies = addReplyToCommentRecursive(
                        comment.replies,
                        parentId,
                        newReply
                    )
                )
            }
        }
    }

    private fun updateCommentContentRecursive(
        comments: List<Comment>,
        commentIdToEdit: String,
        newContent: String
    ): List<Comment> {
        return comments.map { comment ->
            if (comment.id == commentIdToEdit) {
                comment.copy(content = newContent)
            } else {
                comment.copy(
                    replies = updateCommentContentRecursive(
                        comment.replies,
                        commentIdToEdit,
                        newContent
                    )
                )
            }
        }
    }

    fun onDeleteComment(commentId: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    comments = removeCommentRecursive(currentState.comments, commentId)
                )
            }
        }
    }

    private fun removeCommentRecursive(
        comments: List<Comment>,
        commentIdToRemove: String
    ): List<Comment> {
        return comments.filter { it.id != commentIdToRemove }.map { comment ->
            comment.copy(replies = removeCommentRecursive(comment.replies, commentIdToRemove))
        }
    }
}