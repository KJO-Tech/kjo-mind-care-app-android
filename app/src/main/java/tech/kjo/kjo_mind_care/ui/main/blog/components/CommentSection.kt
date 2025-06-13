package tech.kjo.kjo_mind_care.ui.main.blog.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.ThemedButton
import tech.kjo.kjo_mind_care.ui.main.blog.Comment

@Composable
fun CommentSection(
    comments: List<Comment>,
    onReplyToComment: (String) -> Unit,
    onEditComment: (String, String) -> Unit,
    onDeleteComment: (String) -> Unit,
    showCommentInput: Boolean,
    commentToReplyTo: String?,
    commentToEdit: String?,
    currentCommentText: String,
    onCommentTextChanged: (String) -> Unit,
    onSaveComment: () -> Unit,
    onCancelCommentInput: () -> Unit,
    onAddCommentClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(
                R.string.comments_section_title,
                comments.size
            ),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Botón "Añadir comentario" si no hay input activo
        if (!showCommentInput) {
            ThemedButton(
                text = stringResource(R.string.add_comment_button),
                onClick = onAddCommentClick
            )
        }

        // Input para crear/responder/editar comentarios
        AnimatedVisibility(
            visible = showCommentInput && (commentToReplyTo == null && commentToEdit == null),
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            // Este es el input global para añadir un nuevo comentario principal
            CommentInput(
                currentText = currentCommentText,
                onTextChanged = onCommentTextChanged,
                onSave = onSaveComment,
                onCancel = onCancelCommentInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        comments.forEach { comment ->
            CommentItem(
                comment = comment,
                onReplyToComment = onReplyToComment,
                onEditComment = onEditComment,
                onDeleteComment = onDeleteComment,
                showCommentInput = showCommentInput,
                commentToReplyTo = commentToReplyTo,
                commentToEdit = commentToEdit,
                currentCommentText = currentCommentText,
                onCommentTextChanged = onCommentTextChanged,
                onSaveComment = onSaveComment,
                onCancelCommentInput = onCancelCommentInput,
                isRoot = true
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onReplyToComment: (String) -> Unit,
    onEditComment: (String, String) -> Unit,
    onDeleteComment: (String) -> Unit,
    showCommentInput: Boolean,
    commentToReplyTo: String?,
    commentToEdit: String?,
    currentCommentText: String,
    onCommentTextChanged: (String) -> Unit,
    onSaveComment: () -> Unit,
    onCancelCommentInput: () -> Unit,
    isRoot: Boolean
) {
    // Estado para controlar la visibilidad del diálogo de confirmación
    var showDeleteDialog by remember { mutableStateOf<Comment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .let {
                if (!isRoot) it.padding(start = 16.dp) else it // Indentación para anidados
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Avatar(user = comment.author, size = 32)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.author.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.getTimeAgo(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Row(horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = { onReplyToComment(comment.id) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = stringResource(R.string.content_description_reply),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (comment.isMine) {
                    IconButton(
                        onClick = { onEditComment(comment.id, comment.content) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.content_description_edit),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = comment }, // Mostrar diálogo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.content_description_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = if (!isRoot) (16 + 2 + 8).dp else 0.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input para responder o editar este comentario específico
        AnimatedVisibility(
            visible = showCommentInput && (commentToReplyTo == comment.id || commentToEdit == comment.id),
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            CommentInput(
                currentText = currentCommentText,
                onTextChanged = onCommentTextChanged,
                onSave = onSaveComment,
                onCancel = onCancelCommentInput,
                isEditing = commentToEdit == comment.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .let {
                        if (!isRoot) it.padding(start = 16.dp + 2.dp + 8.dp) else it // Indentación para el input anidado
                    }
            )
        }

        // Comentarios anidados
        if (comment.replies.isNotEmpty()) {
            comment.replies.forEach { reply ->
                CommentItem(
                    comment = reply,
                    onReplyToComment = onReplyToComment,
                    onEditComment = onEditComment,
                    onDeleteComment = onDeleteComment,
                    showCommentInput = showCommentInput,
                    commentToReplyTo = commentToReplyTo,
                    commentToEdit = commentToEdit,
                    currentCommentText = currentCommentText,
                    onCommentTextChanged = onCommentTextChanged,
                    onSaveComment = onSaveComment,
                    onCancelCommentInput = onCancelCommentInput,
                    isRoot = false
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { commentToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null }, // Cerrar diálogo al tocar fuera
            title = { Text(stringResource(R.string.delete_comment_dialog_title)) },
            text = { Text(stringResource(R.string.delete_comment_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteComment(commentToDelete.id)
                        showDeleteDialog = null // Cerrar diálogo después de confirmar
                    }
                ) {
                    Text(
                        stringResource(R.string.delete_comment_dialog_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null } // Cerrar diálogo al cancelar
                ) {
                    Text(stringResource(R.string.delete_comment_dialog_cancel))
                }
            }
        )
    }
}

@Composable
fun CommentInput(
    currentText: String,
    onTextChanged: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isEditing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = currentText,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester), // Aplicar FocusRequester
            placeholder = { Text(stringResource(R.string.write_comment_placeholder)) },
            maxLines = 5,
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onCancel) {
                Text(stringResource(R.string.cancel_comment_button))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onSave, enabled = currentText.isNotBlank()) {
                Text(stringResource(R.string.save_comment_button))
            }
        }
    }

    // Efecto para solicitar foco y abrir teclado cuando el componente es visible
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}