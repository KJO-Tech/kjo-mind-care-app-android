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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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

    private val _rawBlogsFromDb = getBlogPostsUseCase()

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
            _rawBlogsFromDb
                .combine(_uiState.map { it.currentUser }.distinctUntilChanged()) { rawBlogs, currentUser ->
                    rawBlogs.map { blog ->
                        val isLikedByUser = if (currentUser != null) {
                            reactionRepository.hasUserLikedBlog(blog.id, currentUser.uid).getOrDefault(false)
                        } else {
                            false
                        }
                        blog.copy(isLiked = isLikedByUser)
                    }
                }
                .collect { blogsWithLikedStatus ->
                    _uiState.update { it.copy(allBlogs = blogsWithLikedStatus) }
                }
        }

        viewModelScope.launch {
            _uiState.map { it.allBlogs }
                .distinctUntilChanged()
                .collect { blogs ->
                    val newCommentCounts = mutableMapOf<String, Int>()
                    blogs.forEach { blog ->
                        _uiState.value.blogCommentCounts[blog.id]?.let {
                            newCommentCounts[blog.id] = it
                        } ?: run {
                            launch {
                                getCommentsForBlogUseCase(blog.id)
                                    .collect { comments ->
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
                    _uiState.update { it.copy(blogCommentCounts = newCommentCounts) }
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

            val currentBlog = _uiState.value.filteredBlogs.find { it.id == blogId }
                ?: _uiState.value.allBlogs.find { it.id == blogId }

            if (currentBlog == null) {
                _uiState.update { it.copy(errorMessage = "Blog no encontrado en la lista para dar like.") }
                return@launch
            }

            val initialReactionCount = currentBlog.reaction
            val initialIsLiked = currentBlog.isLiked

            val newReactionCount = initialReactionCount + if (initialIsLiked) -1 else 1
            val newIsLiked = !initialIsLiked

            _uiState.update { currentState ->
                currentState.copy(
                    filteredBlogs = currentState.filteredBlogs.map { blog ->
                        if (blog.id == blogId) {
                            blog.copy(
                                reaction = newReactionCount,
                                isLiked = newIsLiked
                            )
                        } else {
                            blog
                        }
                    },
                    allBlogs = currentState.allBlogs.map { blog ->
                        if (blog.id == blogId) {
                            blog.copy(
                                reaction = newReactionCount,
                                isLiked = newIsLiked
                            )
                        } else {
                            blog
                        }
                    }
                )
            }

            toggleBlogLikeUseCase(blogId, currentUser.uid)
                .onSuccess {
                    _uiState.update { it.copy(errorMessage = null) }
                }
                .onFailure { e ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            filteredBlogs = currentState.filteredBlogs.map { blog ->
                                if (blog.id == blogId) {
                                    blog.copy(
                                        reaction = initialReactionCount,
                                        isLiked = initialIsLiked
                                    )
                                } else {
                                    blog
                                }
                            },
                            allBlogs = currentState.allBlogs.map { blog ->
                                if (blog.id == blogId) {
                                    blog.copy(
                                        reaction = initialReactionCount,
                                        isLiked = initialIsLiked
                                    )
                                } else {
                                    blog
                                }
                            },
                            errorMessage = "Error al dar/quitar like: ${e.localizedMessage}"
                        )
                    }
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