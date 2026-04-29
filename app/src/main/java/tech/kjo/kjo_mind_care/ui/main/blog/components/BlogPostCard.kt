package tech.kjo.kjo_mind_care.ui.main.blog.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Blog

@Composable
fun BlogPostCard(
    blog: Blog,
    onBlogClick: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onBlogShare: (String, String) -> Unit,
    commentCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBlogClick(blog.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            BlogMediaPreview(
                mediaUrl = blog.mediaUrl,
                mediaType = blog.mediaType,
                isDetailScreen = false
            )
            if (blog.mediaUrl != null && blog.mediaUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(user = blog.author)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = blog.author.fullName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = blog.getTimeAgo(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = blog.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido limitado a 3 líneas para la Card
            Text(
                text = blog.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis // Añade puntos suspensivos si el texto es muy largo
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onToggleLike(blog.id) }
                    ) {
                        Icon(
                            imageVector = if (blog.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.content_description_like_button),
                            tint = if (blog.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = blog.reaction.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = { onBlogClick(blog.id) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = stringResource(R.string.content_description_comment_button)
                        )
                    }

                    Text(
                        text = commentCount.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    onClick = { onBlogShare(blog.id, blog.title) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.content_description_share_button)
                    )
                }
            }
        }
    }
}