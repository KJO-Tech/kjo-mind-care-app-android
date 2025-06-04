package tech.kjo.kjo_mind_care.ui.main.blog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BlogList() {
    val blogs = remember {
        listOf(
            BlogPost(
                id = 1,
                title = "Cómo manejar la ansiedad en exámenes",
                content = "La ansiedad ante los exámenes es una respuesta común que muchos estudiantes experimentan. Aquí hay algunas estrategias que pueden ayudarte a manejarla...",
                author = "Carlos M.",
                authorAvatar = "",
                timeAgo = "2 horas",
                likes = 24,
                comments = 8,
                isLiked = false
            ),
            BlogPost(
                id = 2,
                title = "Técnicas de respiración para momentos de estrés",
                content = "La respiración consciente es una herramienta poderosa para calmar la mente y reducir el estrés. Estas técnicas simples pueden practicarse en cualquier lugar...",
                author = "Ana P.",
                authorAvatar = "",
                timeAgo = "5 horas",
                likes = 42,
                comments = 15,
                isLiked = true
            ),
            BlogPost(
                id = 3,
                title = "Mi experiencia con la meditación diaria",
                content = "Hace seis meses comencé a meditar todos los días durante 10 minutos. Al principio fue difícil mantener la constancia, pero los beneficios que he experimentado son...",
                author = "Miguel L.",
                authorAvatar = "",
                timeAgo = "1 día",
                likes = 36,
                comments = 12,
                isLiked = false
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(blogs) { blog ->
            BlogPostCard(blog = blog)
        }
    }
}

data class BlogPost(
    val id: Int,
    val title: String,
    val content: String,
    val author: String,
    val authorAvatar: String,
    val timeAgo: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean
)
