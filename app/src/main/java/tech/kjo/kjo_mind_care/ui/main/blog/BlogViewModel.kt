package tech.kjo.kjo_mind_care.ui.main.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class BlogUiState(
    val selectedTabIndex: Int = 0,
    val searchQuery: String = "",
    val allBlogs: List<BlogPost> = emptyList(), // Lista completa de blogs
    val filteredBlogs: List<BlogPost> = emptyList(), // Lista de blogs filtrada por búsqueda/pestaña
    val isLoading: Boolean = false, // Para un futuro indicador de carga
    val error: String? = null // Para futuros mensajes de error
)

@OptIn(FlowPreview::class)
class BlogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BlogUiState())
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    // Los datos estáticos (futuramente cargados de un repositorio)
    private val _allBlogs = MutableStateFlow(StaticBlogData.getSampleBlogPosts())

    init {
        // Combinamos el estado de búsqueda y pestaña con la lista de blogs
        viewModelScope.launch {
            _uiState.debounce(300) // Pequeño debounce para la búsqueda
                .combine(_allBlogs) { uiState, allBlogs ->
                    val filteredBySearch = if (uiState.searchQuery.isNotBlank()) {
                        allBlogs.filter {
                            it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                                    it.content.contains(uiState.searchQuery, ignoreCase = true) ||
                                    it.author.fullName.contains(uiState.searchQuery, ignoreCase = true) ||
                                    it.author.username.contains(uiState.searchQuery, ignoreCase = true)
                        }
                    } else {
                        allBlogs
                    }

                    // Filtrar por pestaña (lógica simple, se puede expandir)
                    val finalFilteredBlogs = when (uiState.selectedTabIndex) {
                        0 -> filteredBySearch // All
                        1 -> filteredBySearch.sortedByDescending { it.likes } // Popular
                        2 -> filteredBySearch.sortedByDescending { it.createdAt } // Latest
                        3 -> filteredBySearch.filter { it.author.id == "user_current" } // My Blogs (simulado)
                        else -> filteredBySearch
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

    // Funciones para manejar interacciones (simuladas por ahora)
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
}

object StaticBlogData {
    private val currentUser = User(id = "user_current", username = "kjo_dev", fullName = "Kjo Developer", profileImageUrl = null) // Sin imagen para test

    fun getSampleBlogPosts(): List<BlogPost> {
        return listOf(
            BlogPost(
                id = "blog_1",
                title = "Cómo manejar la ansiedad en exámenes",
                content = "La ansiedad ante los exámenes es una respuesta común que muchos estudiantes experimentan. Aquí hay algunas estrategias que pueden ayudarte a manejarla...",
                author = User(id = "user_1", username = "carlos_m", fullName = "Carlos M.", profileImageUrl = "https://randomuser.me/api/portraits/men/1.jpg"),
                createdAt = LocalDateTime.now().minusHours(2),
                mediaUrl = "https://santiagocidpsicologia.com/wp-content/uploads/ansiedad-ante-los-examanes.jpg",
                mediaType = MediaType.IMAGE,
                likes = 24,
                comments = 8,
                isLiked = false
            ),
            BlogPost(
                id = "blog_2",
                title = "Técnicas de respiración para momentos de estrés",
                content = "La respiración consciente es una herramienta poderosa para calmar la mente y reducir el estrés. Estas técnicas simples pueden practicarse en cualquier lugar...",
                author = User(id = "user_2", username = "ana_p", fullName = "Ana P.", profileImageUrl = "https://randomuser.me/api/portraits/women/2.jpg"),
                createdAt = LocalDateTime.now().minusHours(5),
                mediaUrl = null, // Sin imagen/video
                mediaType = null,
                likes = 42,
                comments = 15,
                isLiked = true
            ),
            BlogPost(
                id = "blog_3",
                title = "Mi experiencia con la meditación diaria",
                content = "Hace seis meses comencé a meditar todos los días durante 10 minutos. Al principio fue difícil mantener la constancia, pero los beneficios que he experimentado son...",
                author = User(id = "user_3", username = "miguel_l", fullName = "Miguel L.", profileImageUrl = "https://randomuser.me/api/portraits/men/3.jpg"),
                createdAt = LocalDateTime.now().minusDays(1),
                mediaUrl = "https://videos.pexels.com/video-files/3712701/3712701-uhd_2160_4096_25fps.mp4",
                mediaType = MediaType.VIDEO,
                likes = 36,
                comments = 12,
                isLiked = false
            ),
            BlogPost(
                id = "blog_4",
                title = "Beneficios de una dieta saludable para la mente",
                content = "La alimentación juega un papel crucial en nuestra salud mental. Descubre cómo ciertos alimentos pueden mejorar tu estado de ánimo y concentración...",
                author = User(id = "user_4", username = "laura_g", fullName = "Laura G.", profileImageUrl = "https://randomuser.me/api/portraits/women/4.jpg"),
                createdAt = LocalDateTime.now().minusDays(3),
                mediaUrl = "https://mejorconsalud.as.com/wp-content/uploads/2017/05/alimentos-salud-cerebro.jpg",
                mediaType = MediaType.IMAGE,
                likes = 50,
                comments = 20,
                isLiked = false
            ),
            BlogPost(
                id = "blog_5",
                title = "Superando el insomnio: una guía práctica",
                content = "El sueño es fundamental para el bienestar. Si luchas contra el insomnio, estas estrategias y consejos pueden ayudarte a mejorar la calidad de tu descanso...",
                author = currentUser, // Este blog es del usuario actual
                createdAt = LocalDateTime.now().minusWeeks(1),
                mediaUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ5mVtqidHO1-jrCEr0YlO54WbvQarvu4QAhA&s",
                mediaType = MediaType.IMAGE,
                likes = 60,
                comments = 25,
                isLiked = true
            )
        )
    }
}