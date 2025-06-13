package tech.kjo.kjo_mind_care.ui.main.blog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

data class BlogDetailUiState(
    val blogPost: BlogPost? = null,
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCommentInput: Boolean = false, // Para mostrar/ocultar el input de comentario
    val commentToReplyTo: String? = null, // ID del comentario al que se responde
    val commentToEdit: String? = null, // ID del comentario a editar
    val currentCommentText: String = "" // Texto actual del input de comentario
)

class BlogDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val blogId: String = checkNotNull(savedStateHandle["blogId"])

    private val _uiState = MutableStateFlow(BlogDetailUiState())
    val uiState: StateFlow<BlogDetailUiState> = _uiState.asStateFlow()

    private val currentUser = StaticBlogDetailData.currentUser // Asume un usuario logueado

    init {
        loadBlogDetail()
    }

    private fun loadBlogDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val blog = StaticBlogData.getSampleBlogPosts().find { it.id == blogId }
                val comments = StaticBlogDetailData.getSampleCommentsForBlog(blogId)
                _uiState.update { it.copy(blogPost = blog, comments = comments, isLoading = false) }
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
                // Manejar error o mensaje al usuario: el comentario no puede estar vacío
                return@launch
            }

            if (_uiState.value.commentToEdit != null) {
                // Lógica para editar un comentario existente
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
                // Lógica para añadir un nuevo comentario o responder
                val newComment = Comment(
                    id = UUID.randomUUID().toString(), // Generar ID único
                    author = currentUser,
                    content = currentText,
                    createdAt = LocalDateTime.now(),
                    isMine = true
                )

                _uiState.update { currentState ->
                    val updatedComments = if (_uiState.value.commentToReplyTo != null) {
                        // Es una respuesta
                        addReplyToCommentRecursive(
                            currentState.comments,
                            _uiState.value.commentToReplyTo!!,
                            newComment
                        )
                    } else {
                        // Es un comentario de nivel superior
                        currentState.comments + newComment
                    }
                    currentState.copy(comments = updatedComments)
                }
            }

            // Limpiar y ocultar el input
            onCancelCommentInput()
        }
    }

    // Funciones helper recursivas para comentarios anidados
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
            // Lógica para eliminar un comentario propio
            _uiState.update { currentState ->
                currentState.copy(
                    comments = removeCommentRecursive(currentState.comments, commentId)
                )
            }
            // En una app real: llamar a un repositorio para eliminar en el backend
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


// Datos estáticos de ejemplo para comentarios
object StaticBlogDetailData {
    val user1 = User(
        id = "user_1",
        username = "carlos_m",
        fullName = "Carlos M.",
        profileImageUrl = "https://randomuser.me/api/portraits/men/1.jpg"
    )
    val user2 = User(
        id = "user_2",
        username = "ana_p",
        fullName = "Ana P.",
        profileImageUrl = "https://randomuser.me/api/portraits/women/2.jpg"
    )
    val currentUser = User(
        id = "user_current",
        username = "kjo_dev",
        fullName = "Kjo Developer",
        profileImageUrl = null
    )


    fun getSampleCommentsForBlog(blogId: String): List<Comment> {
        return when (blogId) {
            "blog_1" -> listOf(
                Comment(
                    id = "comment_1_1",
                    author = user1,
                    content = "¡Excelente artículo! Muy útil para la temporada de exámenes.",
                    createdAt = LocalDateTime.now().minusHours(1),
                    isMine = false,
                    replies = listOf(
                        Comment(
                            id = "comment_1_1_1",
                            author = currentUser,
                            content = "Me alegro de que te sea útil, ¡gracias por el comentario!",
                            createdAt = LocalDateTime.now().minusMinutes(30),
                            isMine = true
                        )
                    )
                ),
                Comment(
                    id = "comment_1_2",
                    author = user2,
                    content = "La parte de la respiración me ayudó mucho.",
                    createdAt = LocalDateTime.now().minusMinutes(45),
                    isMine = false
                )
            )

            "blog_3" -> listOf(
                Comment(
                    id = "comment_3_1",
                    author = currentUser,
                    content = "¡Qué interesante! Justo lo que necesitaba leer.",
                    createdAt = LocalDateTime.now().minusDays(1),
                    isMine = true
                )
            )

            else -> emptyList()
        }
    }
}