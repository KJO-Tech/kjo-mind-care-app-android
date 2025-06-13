package tech.kjo.kjo_mind_care.ui.main.blog.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.main.blog.MediaType

@Composable
fun BlogMediaPreview(
    mediaUrl: String?,
    mediaType: MediaType?,
    isDetailScreen: Boolean = false // Nuevo parámetro para controlar el tamaño/funcionalidad
) {
    if (mediaUrl == null || mediaUrl.isBlank()) {
        return // No hay contenido multimedia para mostrar
    }

    val context = LocalContext.current
    val aspectRatio = if (isDetailScreen) 16f / 9f else 16f / 9f

    when (mediaType) {
        MediaType.IMAGE -> {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(mediaUrl)
                    .apply(block = {
                        crossfade(true)
                    }).build()
            )
            Image(
                painter = painter,
                contentDescription = stringResource(R.string.content_description_blog_preview_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        MediaType.VIDEO -> {
            if (isDetailScreen) {
                // Mini-reproductor de video para la pantalla de detalle
                val exoPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(Uri.parse(mediaUrl)))
                        prepare()
                        playWhenReady = false // No reproducir automáticamente
                    }
                }

                DisposableEffect(
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true // Mostrar controles de reproducción
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio)
                            .clip(RoundedCornerShape(8.dp))
                    )
                ) {
                    onDispose {
                        exoPlayer.release() // Libera el reproductor cuando el Composable deja de existir
                    }
                }
            } else {
                // Solo miniatura y icono de reproducción para la lista/card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Puedes intentar cargar una miniatura del video si la URL lo permite (ej. YouTube thumbnail)
                    // Para demostración, solo usamos un fondo oscuro.
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.content_description_play_video), // Accesibilidad
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        null -> { /* No hacer nada */ }
    }
}