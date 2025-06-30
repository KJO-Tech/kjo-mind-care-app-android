package tech.kjo.kjo_mind_care.ui.components

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerWebView(
    modifier: Modifier = Modifier,
    videoId: String
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                settings.javaScriptEnabled = true // Necesario para YouTube
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture =
                    false // Permite auto-reproducción si el contenido lo permite

                webChromeClient =
                    WebChromeClient() // Para manejar el fullscreen y otras características de JS
                webViewClient = WebViewClient() // Para manejar la navegación interna

                // Esto es un iframe de YouTube para incrustar el video
                val htmlData = """
                    <!DOCTYPE html>
                    <html>
                    <body style="margin:0;padding:0;">
                    <iframe 
                        width="100%" 
                        height="auto" 
                        src="https://www.youtube.com/embed/$videoId?autoplay=1&controls=1&fs=1&modestbranding=1&rel=0" 
                        frameborder="0" 
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                        allowfullscreen>
                    </iframe>
                    </body>
                    </html>
                """.trimIndent()
                loadData(htmlData, "text/html", "utf-8")
            }
        }
    )
}

/**
 * Función auxiliar mejorada para extraer el ID de video de YouTube de una URL.
 * Soporta múltiples formatos de URL de YouTube y valida que el ID tenga 11 caracteres.
 */
fun getYouTubeVideoId(youtubeUrl: String): String? {
    if (youtubeUrl.isBlank()) return null

    val patterns = listOf(
        // URL estándar: https://www.youtube.com/watch?v=VIDEO_ID
        "(?:youtube\\.com/watch\\?v=)([a-zA-Z0-9_-]{11})",
        // URL corta: https://youtu.be/VIDEO_ID
        "(?:youtu\\.be/)([a-zA-Z0-9_-]{11})",
        // URL embed: https://www.youtube.com/embed/VIDEO_ID
        "(?:youtube\\.com/embed/)([a-zA-Z0-9_-]{11})",
        // URL con parámetros adicionales
        "(?:youtube\\.com/watch\\?.*v=)([a-zA-Z0-9_-]{11})",
        // URL móvil
        "(?:m\\.youtube\\.com/watch\\?v=)([a-zA-Z0-9_-]{11})"
    )

    for (pattern in patterns) {
        try {
            val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
            val match = regex.find(youtubeUrl)
            if (match != null && match.groupValues.size > 1) {
                val videoId = match.groupValues[1]
                // Validar que el ID tenga exactamente 11 caracteres
                if (videoId.length == 11) {
                    return videoId
                }
            }
        } catch (e: Exception) {
            Log.w("YouTubePlayer", "Error parsing URL with pattern $pattern: ${e.message}")
        }
    }

    return null
}

/**
 * Función para validar si una URL de YouTube es válida
 */
fun isValidYouTubeUrl(url: String): Boolean {
    return getYouTubeVideoId(url) != null
}