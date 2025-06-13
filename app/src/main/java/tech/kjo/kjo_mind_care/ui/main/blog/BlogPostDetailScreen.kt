package tech.kjo.kjo_mind_care.ui.main.blog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.main.blog.components.Avatar
import tech.kjo.kjo_mind_care.ui.main.blog.components.BlogMediaPreview
import tech.kjo.kjo_mind_care.ui.main.blog.components.CommentSection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogPostDetailScreen(
    blogId: String,
    onNavigateBack: () -> Unit,
    viewModel: BlogDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Este LaunchedEffect asegura que el ViewModel reciba el blogId si no lo hizo por SavedStateHandle
    // O si el ViewModel se inicializa en otro punto y necesita el ID.
    // Sin embargo, con SavedStateHandle, el ViewModel ya tiene el blogId al ser creado.
    // Lo mantenemos aquí para claridad si el ViewModel fuera a recargar en base a un cambio de ID.
    LaunchedEffect(blogId) {
        // ViewModel ya maneja la carga con SavedStateHandle
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.blog_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        stringResource(R.string.loading_blog),
                        modifier = Modifier.padding(top = 70.dp)
                    )
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.error_loading_blog, uiState.error ?: ""),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.blogPost == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.blog_not_found))
                }
            }

            else -> {
                val blog = uiState.blogPost!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        BlogMediaPreview(
                            mediaUrl = blog.mediaUrl,
                            mediaType = blog.mediaType,
                            isDetailScreen = true
                        )
                        if (blog.mediaUrl != null && blog.mediaUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Título del Blog
                        Text(
                            text = blog.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Autor y Fecha
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(user = blog.author, size = 48)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = blog.author.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = blog.getTimeAgo(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones de acción del blog (Likes, Comentarios, Compartir)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(
                                onClick = { viewModel.toggleLike() },
                                contentPadding = PaddingValues(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ) // Menos padding
                            ) {
                                Icon(
                                    imageVector = if (blog.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = stringResource(R.string.content_description_like_button),
                                    tint = if (blog.isLiked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(blog.likes.toString())
                            }
                            Button(
                                onClick = { /* TODO: Posiblemente navegar a los comentarios si están en otra pantalla o hacer scroll */ },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Comment,
                                    contentDescription = stringResource(R.string.content_description_comment_button)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(blog.comments.toString())
                            }
                            Button(
                                onClick = { /* TODO: Compartir */ },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = stringResource(R.string.content_description_share_button)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(R.string.content_description_share_button))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Contenido Completo del Blog (sin límite de líneas)
                        Text(
                            text = blog.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección de Comentarios
                        CommentSection(
                            comments = uiState.comments,
                            onReplyToComment = { commentId -> viewModel.onReplyToComment(commentId) },
                            onEditComment = { commentId, currentContent ->
                                viewModel.onEditComment(
                                    commentId,
                                    currentContent
                                )
                            },
                            onDeleteComment = { commentId -> viewModel.onDeleteComment(commentId) },
                            showCommentInput = uiState.showCommentInput,
                            commentToReplyTo = uiState.commentToReplyTo,
                            commentToEdit = uiState.commentToEdit,
                            currentCommentText = uiState.currentCommentText,
                            onCommentTextChanged = { newText ->
                                viewModel.onCommentTextChanged(
                                    newText
                                )
                            },
                            onSaveComment = { viewModel.onSaveComment() },
                            onCancelCommentInput = { viewModel.onCancelCommentInput() },
                            onAddCommentClick = { viewModel.onAddCommentClick() }
                        )
                    }
                }
            }
        }
    }
}