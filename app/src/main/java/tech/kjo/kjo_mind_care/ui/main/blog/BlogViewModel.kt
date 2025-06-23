package tech.kjo.kjo_mind_care.ui.main.blog

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.BlogPost
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import tech.kjo.kjo_mind_care.data.repository.CategoryRepository
import tech.kjo.kjo_mind_care.ui.navigation.Screen

data class BlogUiState(
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val filteredBlogs: List<BlogPost> = emptyList(),
    val isRefreshing: Boolean = false,
    val availableCategories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val showCategoryFilterDialog: Boolean = false
)

@OptIn(FlowPreview::class)
class BlogViewModel(
    private val categoryRepository: CategoryRepository = CategoryRepository() // Inyectar o crear
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlogUiState())
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    private val _allBlogs = MutableStateFlow(StaticBlogData.getSampleBlogPosts())

    init {
        viewModelScope.launch {
            // Observar las categorías disponibles del repositorio
            categoryRepository.categories.collect { categories ->
                _uiState.update { it.copy(availableCategories = categories.filter { cat -> cat.isActive }) }
            }
        }

        viewModelScope.launch {
            _uiState
                .debounce(300)
                .combine(_allBlogs) { uiState, allBlogs ->
                    val filteredBySearch = if (uiState.searchQuery.isNotBlank()) {
                        allBlogs.filter {
                            it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                                    it.content.contains(uiState.searchQuery, ignoreCase = true) ||
                                    it.author.fullName.contains(
                                        uiState.searchQuery,
                                        ignoreCase = true
                                    )
//                                    ||
//                                    it.author.username.contains(
//                                        uiState.searchQuery,
//                                        ignoreCase = true
//                                    )
                        }
                    } else {
                        allBlogs
                    }

                    // Filtrado por categoría: ahora un solo ID de categoría
                    val filteredByCategories = if (uiState.selectedCategoryId != null) {
                        filteredBySearch.filter { blog ->
                            blog.categoryId == uiState.selectedCategoryId
                        }
                    } else {
                        filteredBySearch
                    }

                    val finalFilteredBlogs = when (uiState.selectedTabIndex) {
                        0 -> filteredByCategories
                        1 -> filteredByCategories.sortedByDescending { it.likes }
                        2 -> filteredByCategories.sortedByDescending { it.createdAt }
                        3 -> filteredByCategories.filter { it.author.uid == StaticBlogData.currentUser.uid }
                        else -> filteredByCategories
                    }
                    finalFilteredBlogs
                }
                .collect { filteredList ->
                    _uiState.update { it.copy(filteredBlogs = filteredList) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun toggleLike(blogId: String) {
        _allBlogs.update { blogs ->
            blogs.map { blog ->
                if (blog.id == blogId) {
                    blog.copy(
                        isLiked = !blog.isLiked,
                        likes = if (blog.isLiked) blog.likes - 1 else blog.likes + 1
                    )
                } else blog
            }
        }
    }

    fun refreshBlogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            delay(2000)
            _allBlogs.update { StaticBlogData.getSampleBlogPosts() }
            categoryRepository.refreshCategories() // Refrescar también las categorías
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun showCategoryFilterDialog(show: Boolean) {
        _uiState.update { it.copy(showCategoryFilterDialog = show) }
    }

    // Función para seleccionar una única categoría
    fun selectCategory(categoryId: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategoryId = categoryId,
                showCategoryFilterDialog = false // Cierra el diálogo al seleccionar
            )
        }
    }

    // Para deseleccionar la categoría (el botón "Borrar selección")
    fun clearCategorySelection() {
        _uiState.update { it.copy(selectedCategoryId = null, showCategoryFilterDialog = false) }
    }

    fun shareBlog(context: Context, blogId: String, blogTitle: String) {
        // Usamos el DEEPLINK_WEB_PATTERN como el enlace que queremos compartir
        // Aunque aún no tengas un dominio web configurado, este es el formato correcto
        // para cuando lo tengas. Por ahora, si no tienes dominio, el sistema
        // intentará abrir tu app si está instalada y maneja el esquema personalizado,
        // o simplemente mostrará el texto sin abrir una app específica.
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