package tech.kjo.kjo_mind_care.ui.main.blog_detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.enums.BlogStatus
import tech.kjo.kjo_mind_care.ui.main.blog.components.Avatar
import tech.kjo.kjo_mind_care.ui.main.blog.components.BlogMediaPreview
import tech.kjo.kjo_mind_care.ui.main.blog.components.CommentSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogPostDetailScreen(
    blogId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditBlog: (String) -> Unit,
    viewModel: BlogDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(message = it, withDismissAction = true)
        }
    }
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            snackbarHostState.showSnackbar(
//                message = stringResource(R.string.blog_deleted_success),
                message = "Blog eliminado exitosamente.",
                withDismissAction = true
            )
            onNavigateBack() // Volver atrás después de eliminar
            // Importante: No hay necesidad de resetear deleteSuccess si ya se va a salir de la pantalla
        }
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
                actions = {
                    // Botón de Compartir
                    IconButton(onClick = {
                        uiState.blogPost?.let { blog ->
                            viewModel.shareBlog(context, blog.id, blog.title)
                        }
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_blog_content_description)
                        )
                    }

                    // Mostrar botones de editar y eliminar si el usuario es el autor y el blog no está eliminado
                    if (viewModel.isCurrentUserAuthor() && uiState.blogPost?.status != BlogStatus.DELETED) {
                        IconButton(
                            onClick = { onNavigateToEditBlog(blogId) },
                            enabled = !uiState.isDeletingBlog // Deshabilitar si se está eliminando
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_blog_button)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.showDeleteConfirmation(true) },
                            enabled = !uiState.isDeletingBlog
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_blog_button)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                        // Mensaje de estado (Pendiente, Eliminado)
                        uiState.blogStatusMessage?.let { message ->
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error, // O un color de advertencia
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

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
                                )
                            ) {
                                Icon(
                                    imageVector = if (blog.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = stringResource(R.string.content_description_like_button),
                                    tint = if (blog.isLiked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(blog.reaction.toString())
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
                                Text(uiState.commentCount.toString())
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
                            onReplyToComment = { comment -> viewModel.onReplyToComment(comment) },
                            onEditComment = { comment -> viewModel.onEditComment(comment) },
                            onDeleteComment = { comment -> viewModel.onDeleteComment(comment) },
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

    // Diálogo de confirmación de eliminación (siempre al final de tu Composable)
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirmation(false) },
            title = { Text(stringResource(R.string.delete_blog_dialog_title)) },
            text = { Text(stringResource(R.string.delete_blog_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = viewModel::deleteBlog,
                    enabled = !uiState.isDeletingBlog,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Resalta el botón de eliminar
                ) {
                    if (uiState.isDeletingBlog) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(R.string.delete_blog_dialog_confirm))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteConfirmation(false) }) {
                    Text(stringResource(R.string.cancel_comment_button))
                }
            }
        )
    }

//    // Diálogo para añadir/editar comentario (también al final del Composable)
//    if (uiState.showCommentInput) {
//        AlertDialog(
//            onDismissRequest = { viewModel.onCancelCommentInput() },
//            title = { Text(stringResource(R.string.dialog_add_comment_title)) },
//            text = {
//                OutlinedTextField(
//                    value = uiState.currentCommentText,
//                    onValueChange = viewModel::onCommentTextChanged,
//                    label = { Text(stringResource(R.string.comment_input_placeholder)) },
//                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
//                )
//            },
//            confirmButton = {
//                Button(
//                    onClick = viewModel::onSaveComment,
//                    enabled = !uiState.isSendingComment && uiState.currentCommentText.isNotBlank()
//                ) {
//                    if (uiState.isSendingComment) {
//                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
//                    } else {
//                        Text(stringResource(R.string.send_comment_button))
//                    }
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { viewModel.onCancelCommentInput() }) {
//                    Text(stringResource(R.string.cancel_comment_button))
//                }
//            }
//        )
//    }
}