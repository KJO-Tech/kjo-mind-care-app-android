package tech.kjo.kjo_mind_care.ui.components

import android.annotation.SuppressLint
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
                settings.mediaPlaybackRequiresUserGesture = false // Permite auto-reproducción si el contenido lo permite

                webChromeClient = WebChromeClient() // Para manejar el fullscreen y otras características de JS
                webViewClient = WebViewClient() // Para manejar la navegación interna

                // Esto es un iframe de YouTube para incrustar el video
                val htmlData = """
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
 * Función auxiliar para extraer el ID de video de YouTube de una URL.
 */
fun getYouTubeVideoId(youtubeUrl: String): String? {
    val regex = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|%2Fvideos%2F|embed%2F|youtu.be%2F|\\/v%2F)[^#&?\\n]*".toRegex()
    val match = regex.find(youtubeUrl)
    return match?.value
}