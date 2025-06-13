package tech.kjo.kjo_mind_care.ui.main.blog.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.User

@Composable
fun Avatar(
    user: User,
    size: Int = 40
) {
    val context = LocalContext.current
    val initials = user.fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2)
        .joinToString("")
        ?: user.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    if (user.profileImageUrl != null && user.profileImageUrl.isNotBlank()) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(user.profileImageUrl)
                .apply(block = {
                    transformations(CircleCropTransformation())
                    crossfade(true)
                }).build()
        )
        Image(
            painter = painter,
            contentDescription = stringResource(
                R.string.content_description_avatar,
                user.fullName
            ), // Accesibilidad
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}