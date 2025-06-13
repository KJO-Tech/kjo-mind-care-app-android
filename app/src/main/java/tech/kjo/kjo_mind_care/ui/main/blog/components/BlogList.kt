package tech.kjo.kjo_mind_care.ui.main.blog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.ui.main.blog.BlogPost

@Composable
fun BlogList(
    blogs: List<BlogPost>,
    onBlogClick: (String) -> Unit,
    onToggleLike: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(blogs, key = { it.id }) { blog ->
            BlogPostCard(
                blog = blog,
                onBlogClick = onBlogClick,
                onToggleLike = onToggleLike
            )
        }
    }
}