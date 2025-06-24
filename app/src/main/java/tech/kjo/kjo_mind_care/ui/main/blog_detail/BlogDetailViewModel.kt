package tech.kjo.kjo_mind_care.ui.main.blog_detail

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.model.Comment
import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.IReactionRepository
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import tech.kjo.kjo_mind_care.usecase.blog.DeleteBlogUseCase
import tech.kjo.kjo_mind_care.usecase.blog.GetBlogByIdUseCase
import tech.kjo.kjo_mind_care.usecase.blog.GetBlogUseCase
import tech.kjo.kjo_mind_care.usecase.reaction.ToggleBlogLikeUseCase
import tech.kjo.kjo_mind_care.usecase.comments.AddCommentUseCase
import tech.kjo.kjo_mind_care.usecase.comments.DeleteCommentUseCase
import tech.kjo.kjo_mind_care.usecase.comments.GetCommentsForBlogUseCase
import tech.kjo.kjo_mind_care.usecase.comments.UpdateCommentUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

data class BlogDetailUiState(
    val blogPost: Blog? = null,
    val comments: List<Comment> = emptyList(),
    val commentCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCommentInput: Boolean = false,
    val commentToReplyTo: Comment? = null,
    val commentToEdit: Comment? = null,
    val currentCommentText: String = "",
    val isSendingComment: Boolean = false,
    val isDeletingBlog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val deleteSuccess: Boolean = false,
    val blogStatusMessage: String? = null,
    val currentUser: User? = null
)

@HiltViewModel
class BlogDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBlogPostByIdUseCase: GetBlogByIdUseCase,
    private val getBlogPostsUseCase: GetBlogUseCase,
    private val deleteBlogPostUseCase: DeleteBlogUseCase,
    private val toggleBlogLikeUseCase: ToggleBlogLikeUseCase,
    private val getCommentsForBlogUseCase: GetCommentsForBlogUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val reactionRepository: IReactionRepository
) : ViewModel() {

    private val blogId: String = checkNotNull(savedStateHandle["blogId"])

    private val _uiState = MutableStateFlow(BlogDetailUiState())
    val uiState: StateFlow<BlogDetailUiState> = _uiState.asStateFlow()

    private val _blogFromDb = getBlogPostsUseCase()
        .map { blogs -> blogs.find { it.id == blogId } }
        .distinctUntilChanged()

    init {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _uiState.update { it.copy(currentUser = user) }
        }

        viewModelScope.launch {
            _blogFromDb.combine(_uiState.map { it.currentUser }.distinctUntilChanged()) { blogFromDb, currentUser ->
                if (blogFromDb != null) {
                    val hasLiked = if (currentUser != null) {
                        reactionRepository.hasUserLikedBlog(blogId, currentUser.uid).getOrDefault(false)
                    } else {
                        false
                    }
                    blogFromDb.copy(isLiked = hasLiked)
                } else {
                    null
                }
            }.collect { combinedBlogWithLikedStatus ->
                _uiState.update { currentState ->
                    currentState.copy(
                        blogPost = combinedBlogWithLikedStatus,
                        blogStatusMessage = if (combinedBlogWithLikedStatus != null) {
                            when (combinedBlogWithLikedStatus.status) {
                                BlogStatus.DELETED -> "Este blog ha sido eliminado."
                                BlogStatus.PENDING -> "Este blog está pendiente de publicación."
                                else -> null
                            }
                        } else {
                            "Este blog ya no existe o fue eliminado."
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            getCommentsForBlogUseCase(blogId)
                .map { rawComments ->
                    val nestedComments = buildCommentTree(rawComments)
                    Pair(nestedComments, rawComments.size)
                }
                .collect { (nestedComments, totalCommentCount) ->
                    _uiState.update { it.copy(
                        comments = nestedComments,
                        commentCount = totalCommentCount
                    ) }
                }
        }
    }

    fun toggleLike() {
        viewModelScope.launch {
            val currentBlog = _uiState.value.blogPost
            val currentUser = _uiState.value.currentUser

            if (currentBlog == null || currentUser == null) {
                _uiState.update { it.copy(error = "No se puede dar like. Blog o usuario no disponibles.") }
                return@launch
            }

            val initialReactionCount = currentBlog.reaction
            val initialIsLiked = currentBlog.isLiked

            val newReactionCount = initialReactionCount + if (initialIsLiked) -1 else 1
            val newIsLiked = !initialIsLiked

            _uiState.update { currentState ->
                currentState.copy(
                    blogPost = currentState.blogPost?.copy(
                        reaction = newReactionCount,
                        isLiked = newIsLiked
                    )
                )
            }

            toggleBlogLikeUseCase(currentBlog.id, currentUser.uid)
                .onSuccess {
                    _uiState.update { it.copy(error = null) }
                }
                .onFailure { e ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            blogPost = currentState.blogPost?.copy(
                                reaction = initialReactionCount,
                                isLiked = initialIsLiked
                            ),
                            error = "Error al dar/quitar like: ${e.localizedMessage}"
                        )
                    }
                }
        }
    }

    fun isCurrentUserAuthor(): Boolean {
        return uiState.value.blogPost?.author?.uid == uiState.value.currentUser?.uid
    }

    fun showDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }

    fun deleteBlog() {
        val blogToDeleteId = uiState.value.blogPost?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingBlog = true, error = null) }
            val result = deleteBlogPostUseCase(blogToDeleteId)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingBlog = false,
                            deleteSuccess = true,
                            showDeleteDialog = false,
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

    fun onReplyToComment(comment: Comment) {
        _uiState.update {
            it.copy(
                showCommentInput = true,
                commentToReplyTo = comment,
                commentToEdit = null,
                currentCommentText = ""
            )
        }
    }

    fun onEditComment(comment: Comment) {
        _uiState.update {
            it.copy(
                showCommentInput = true,
                commentToEdit = comment,
                commentToReplyTo = null,
                currentCommentText = comment.content
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

            val blogId = _uiState.value.blogPost?.id ?: run {
                _uiState.update { it.copy(isSendingComment = false, error = "Blog ID no disponible.") }
                return@launch
            }

            val currentUser = _uiState.value.currentUser ?: run {
                _uiState.update { it.copy(isSendingComment = false, error = "Usuario no autenticado para comentar.") }
                return@launch
            }

            if (_uiState.value.commentToEdit != null) {
                val commentToEdit = _uiState.value.commentToEdit!!
                val updatedComment = commentToEdit.copy(content = currentText)
                updateCommentUseCase(blogId, updatedComment)
                    .onSuccess {
                        _uiState.update { it.copy(error = null) }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(error = "Error al editar el comentario: ${e.localizedMessage}") }
                    }
            } else {
                val parentCommentId = _uiState.value.commentToReplyTo?.id

                val newComment = Comment(
                    author = currentUser,
                    content = currentText,
                    createdAt = Timestamp.now(),
                    parentCommentId = parentCommentId,
                    isMine = true
                )

                addCommentUseCase(blogId, newComment)
                    .onSuccess { commentId ->
                        _uiState.update { it.copy(error = null) }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(error = "Error al añadir comentario: ${e.localizedMessage}") }
                    }
            }
            onCancelCommentInput()
            _uiState.update { it.copy(isSendingComment = false) }
        }
    }

    fun onDeleteComment(comment: Comment) {
        viewModelScope.launch {
            val blogId = _uiState.value.blogPost?.id ?: run {
                _uiState.update { it.copy(error = "Blog ID no disponible para eliminar comentario.") }
                return@launch
            }
            deleteCommentUseCase(blogId, comment.id)
                .onSuccess {
                    _uiState.update { it.copy(error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = "Error al eliminar comentario: ${e.localizedMessage}") }
                }
        }
    }

    fun shareBlog(context: Context, blogId: String, blogTitle: String) {
        val shareLink = Screen.BlogPostDetail.DEEPLINK_WEB_PATTERN.replace("{blogId}", blogId)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Echa un vistazo a este blog: \"$blogTitle\"\n$shareLink")
            type = "text/plain"
        }
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_blog_title)
            )
        )
    }

    private fun buildCommentTree(allComments: List<Comment>): List<Comment> {
        val commentsById = allComments.associateBy { it.id }
        val nestedComments = allComments.filter { it.parentCommentId == null || it.parentCommentId.isBlank() }
            .map { rootComment ->
                rootComment.copy(replies = findReplies(rootComment.id, commentsById))
            }
        return nestedComments.sortedBy { it.createdAt.toDate() }
    }

    private fun findReplies(parentId: String, commentsById: Map<String, Comment>): List<Comment> {
        return commentsById.values
            .filter { it.parentCommentId == parentId }
            .map { reply ->
                reply.copy(replies = findReplies(reply.id, commentsById))
            }
            .sortedBy { it.createdAt.toDate() }
    }
}