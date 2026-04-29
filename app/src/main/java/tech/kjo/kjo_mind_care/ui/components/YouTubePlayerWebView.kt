package tech.kjo.kjo_mind_care.ui.components

import android.annotation.SuppressLint
import android.net.http.SslError
import android.util.Log
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
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
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Configuraciones del WebView
                settings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    allowFileAccess = true
                    allowContentAccess = true
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    javaScriptCanOpenWindowsAutomatically = true
                    setSupportMultipleWindows(true)

                    // Configuraciones adicionales para mejorar la compatibilidad
                    userAgentString =
                        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Mobile Safari/537.36"
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                        Log.d(
                            "WebView",
                            "Console: ${consoleMessage?.message()} at ${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()}"
                        )
                        return true
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        Log.w("WebView", "SSL Error: ${error?.toString()}")
                        handler?.proceed() // Solo para desarrollo/testing
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        Log.e("WebView", "Error: $errorCode - $description for URL: $failingUrl")
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString()
                        Log.d("WebView", "Loading URL: $url")
                        return false
                    }
                }

                val htmlData = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <title>YouTube Player</title>
                        <style>
                            * {
                                margin: 0;
                                padding: 0;
                                box-sizing: border-box;
                            }
                            body {
                                background: #000;
                                font-family: Arial, sans-serif;
                            }
                            .container {
                                width: 100%;
                                height: 100%;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }
                            iframe {
                                position: absolute;
                                top: 0;
                                left: 0;
                                width: 100%;
                                height: 100%;
                                border: none;
                                background: #000;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <iframe 
                                id="youtube-player"
                                src="https://www.youtube.com/embed/$videoId?autoplay=0&controls=1&showinfo=0&rel=0&fs=1&modestbranding=1&playsinline=1&enablejsapi=1&origin=https://www.youtube.com"
                                title="YouTube video player"
                                frameborder="0"
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                allowfullscreen
                                onload="document.getElementById('loading').style.display='none';"
                                onerror="document.getElementById('loading').innerHTML='Error al cargar el video';">
                            </iframe>
                        </div>
                    </body>
                    </html>
                """.trimIndent()

                try {
                    loadDataWithBaseURL(
                        "https://www.youtube.com/",
                        htmlData,
                        "text/html",
                        "UTF-8",
                        null
                    )
                } catch (e: Exception) {
                    Log.e("WebView", "Error loading data: ${e.message}")
                    // Fallback: intentar con loadData simple
                    loadData(htmlData, "text/html", "UTF-8")
                }
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