package tech.kjo.kjo_mind_care.ui.main.blog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.data.model.BlogPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogList(
    blogs: List<BlogPost>,
    onBlogClick: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    selectedTabIndex: Int
) {
    val listState = rememberLazyListState()

    // Este LaunchedEffect asegura que el scroll se reinicie a la parte superior
    // cada vez que la pestaña seleccionada cambie.
    LaunchedEffect(selectedTabIndex) {
        listState.scrollToItem(0)
    }

    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        contentAlignment = Alignment.TopCenter,
        // Puedes personalizar el 'indicator' si quieres, por defecto usa PullToRefreshDefaults.Indicator
        // indicator = {
        //     PullToRefreshDefaults.Indicator(
        //         state = pullRefreshState,
        //         isRefreshing = isRefreshing
        //     )
        // }
    ) {
        LazyColumn(
            state = listState,
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
}
