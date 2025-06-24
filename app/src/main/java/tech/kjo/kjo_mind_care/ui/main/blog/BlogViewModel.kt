package tech.kjo.kjo_mind_care.ui.main.blog

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.IReactionRepository
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import tech.kjo.kjo_mind_care.usecase.blog.GetBlogUseCase
import tech.kjo.kjo_mind_care.usecase.reaction.ToggleBlogLikeUseCase
import tech.kjo.kjo_mind_care.usecase.category.GetCategoriesUseCase
import tech.kjo.kjo_mind_care.usecase.comments.GetCommentsForBlogUseCase
import tech.kjo.kjo_mind_care.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

data class BlogUiState(
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val allBlogs: List<Blog> = emptyList(),
    val filteredBlogs: List<Blog> = emptyList(),
    val isRefreshing: Boolean = false,
    val availableCategories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val showCategoryFilterDialog: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: User? = null,
    val blogCommentCounts: Map<String, Int> = emptyMap()
)

@HiltViewModel
class BlogViewModel @Inject constructor(
    private val getBlogPostsUseCase: GetBlogUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val toggleBlogLikeUseCase: ToggleBlogLikeUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCommentsForBlogUseCase: GetCommentsForBlogUseCase,
    private val reactionRepository: IReactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlogUiState())
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    private val _allBlogsFromDb = getBlogPostsUseCase()

    init {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _uiState.update { it.copy(currentUser = user) }
        }

        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(availableCategories = categories.filter { cat -> cat.isActive }) }
            }
        }

        viewModelScope.launch {
            getBlogPostsUseCase().collect { rawBlogs ->
                val currentUser = _uiState.value.currentUser

                val blogsWithLikesAndComments = rawBlogs.map { blog ->
                    val isLikedByUser = if (currentUser != null) {
                        reactionRepository.hasUserLikedBlog(blog.id, currentUser.uid).getOrDefault(false)
                    } else {
                        false
                    }
                    blog.copy(isLiked = isLikedByUser)
                }
                _uiState.update { it.copy(allBlogs = blogsWithLikesAndComments) }

                blogsWithLikesAndComments.forEach { blog ->
                    if (!_uiState.value.blogCommentCounts.containsKey(blog.id)) {
                        launch {
                            getCommentsForBlogUseCase(blog.id).collect { comments ->
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        blogCommentCounts = currentState.blogCommentCounts.toMutableMap().apply {
                                            this[blog.id] = comments.size
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        @OptIn(FlowPreview::class)
        _uiState
            .debounce(300)
            .onEach { uiState ->
                val currentBlogs = uiState.allBlogs

                val filteredBySearch = if (uiState.searchQuery.isNotBlank()) {
                    currentBlogs.filter {
                        it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                                it.content.contains(uiState.searchQuery, ignoreCase = true) ||
                                it.author.fullName.contains(uiState.searchQuery, ignoreCase = true)
                    }
                } else {
                    currentBlogs
                }

                val filteredByCategories = if (uiState.selectedCategoryId != null) {
                    filteredBySearch.filter { blog ->
                        blog.categoryId == uiState.selectedCategoryId
                    }
                } else {
                    filteredBySearch
                }

                val finalFilteredBlogs = when (uiState.selectedTabIndex) {
                    0 -> filteredByCategories
                    1 -> filteredByCategories.sortedByDescending { it.reaction }
                    2 -> filteredByCategories.sortedByDescending { it.getLocalDateTime() }
                    3 -> filteredByCategories.filter { it.author.uid == uiState.currentUser?.uid }
                    else -> filteredByCategories
                }

                _uiState.update { it.copy(filteredBlogs = finalFilteredBlogs) }
            }
            .launchIn(viewModelScope)
    }


    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun toggleLike(blogId: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(errorMessage = "Debes iniciar sesión para dar like.") }
                return@launch
            }

            toggleBlogLikeUseCase(blogId, currentUser.uid)
                .onSuccess {
                    _uiState.update { it.copy(errorMessage = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = "Error al dar/quitar like: ${e.localizedMessage}") }
                }
        }
    }

    fun refreshBlogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun showCategoryFilterDialog(show: Boolean) {
        _uiState.update { it.copy(showCategoryFilterDialog = show) }
    }

    fun selectCategory(categoryId: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategoryId = categoryId,
                showCategoryFilterDialog = false
            )
        }
    }

    fun clearCategorySelection() {
        _uiState.update { it.copy(selectedCategoryId = null, showCategoryFilterDialog = false) }
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
}