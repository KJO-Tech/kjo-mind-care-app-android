package tech.kjo.kjo_mind_care.ui.main.blog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.main.blog.components.BlogList
import tech.kjo.kjo_mind_care.utils.getCurrentLanguageCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(
    onNavigateToBlogPostDetail: (String) -> Unit,
    viewModel: BlogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf(
        stringResource(R.string.tab_all_blogs),
        stringResource(R.string.tab_popular_blogs),
        stringResource(R.string.tab_latest_blogs),
        stringResource(R.string.tab_my_blogs)
    )
    val currentLanguageCode = getCurrentLanguageCode()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.blog_community_title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.search_blogs_placeholder)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.content_description_search_icon)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = { viewModel.showCategoryFilterDialog(true) },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.content_description_filter_button),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            TabRow(
                selectedTabIndex = uiState.selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = uiState.selectedTabIndex == index,
                        onClick = { viewModel.onTabSelected(index) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                BlogList(
                    blogs = uiState.filteredBlogs,
                    onBlogClick = onNavigateToBlogPostDetail,
                    onToggleLike = { blogId -> viewModel.toggleLike(blogId) },
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refreshBlogs() },
                    selectedTabIndex = uiState.selectedTabIndex,
                    onBlogShare = { blogId, blogTitle ->
                        viewModel.shareBlog(
                            context,
                            blogId,
                            blogTitle,
                        )
                    },
                    commentCounts = uiState.blogCommentCounts
                )
            }
        }

        // Diálogo de Filtro por Categoría (Actualizado para selección única)
        if (uiState.showCategoryFilterDialog) {
            // Un estado local para la selección temporal
            var tempSelectedCategoryId by remember { mutableStateOf(uiState.selectedCategoryId) }

            AlertDialog(
                onDismissRequest = { viewModel.showCategoryFilterDialog(false) },
                title = { Text(stringResource(R.string.filter_dialog_title)) },
                text = {
                    Column {
                        // Opción para "Todas las categorías" / "Sin filtro"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempSelectedCategoryId = null } // Deseleccionar
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempSelectedCategoryId == null,
                                onClick = { tempSelectedCategoryId = null }
                            )
                            Text(
                                stringResource(R.string.filter_dialog_clear_selection),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        // Lista de categorías dinámicas
                        uiState.availableCategories.forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tempSelectedCategoryId = category.id }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = tempSelectedCategoryId == category.id,
                                    onClick = { tempSelectedCategoryId = category.id }
                                )
                                Text(
                                    category.getLocalizedName(currentLanguageCode),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.selectCategory(tempSelectedCategoryId) }) { // Usamos selectCategory
                        Text(stringResource(R.string.filter_dialog_apply_filters))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showCategoryFilterDialog(false) }) {
                        Text(stringResource(R.string.cancel_comment_button))
                    }
                }
            )
        }
    }
}