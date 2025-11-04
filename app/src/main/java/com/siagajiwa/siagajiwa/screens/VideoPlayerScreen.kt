package com.siagajiwa.siagajiwa.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.ui.theme.White

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayerScreen(
    navController: NavHostController,
    videoUrl: String,
    videoTitle: String
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var reloadTrigger by remember { mutableIntStateOf(0) }

    // Decode URL if it's encoded
    val decodedUrl = remember(videoUrl) {
        try {
            java.net.URLDecoder.decode(videoUrl, "UTF-8")
        } catch (e: Exception) {
            android.util.Log.e("VideoPlayer", "❌ Error decoding URL: $videoUrl", e)
            videoUrl
        }
    }

    // Extract YouTube video ID from URL
    val videoId = remember(decodedUrl) {
        val id = extractYouTubeVideoId(decodedUrl)
        android.util.Log.d("VideoPlayer", "📹 Original URL: $videoUrl")
        android.util.Log.d("VideoPlayer", "🔓 Decoded URL: $decodedUrl")
        android.util.Log.d("VideoPlayer", "🎬 Extracted Video ID: $id")
        id
    }

    // Handle back button
    BackHandler {
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // YouTube Player using WebView (reloadTrigger causes recreation on crash)
        key(reloadTrigger) {
            AndroidView(
                factory = { context ->
                WebView(context).apply {
                    // Use software rendering to avoid renderer crashes
                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                    // Ensure WebView is visible
                    setBackgroundColor(android.graphics.Color.BLACK)

                    webViewClient = object : WebViewClient() {
                        override fun onRenderProcessGone(view: WebView?, detail: android.webkit.RenderProcessGoneDetail?): Boolean {
                            android.util.Log.e("VideoPlayer", "❌ Renderer process crashed. Crashed: ${detail?.didCrash()}, Priority: ${detail?.rendererPriorityAtExit()}")

                            // Clean up the crashed WebView
                            view?.destroy()

                            // Trigger error state and reload
                            hasError = true
                            errorMessage = "Video player crashed. Tap to reload."

                            // Auto-reload after a short delay
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                android.util.Log.d("VideoPlayer", "🔄 Auto-reloading after crash...")
                                hasError = false
                                reloadTrigger++
                            }, 2000)

                            // Return true to indicate we handled the crash
                            return true
                        }
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            android.util.Log.d("VideoPlayer", "✅ Page finished loading: $url")
                            isLoading = false
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            android.util.Log.d("VideoPlayer", "🔄 Page started loading: $url")
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            android.util.Log.e("VideoPlayer", "❌ WebView error: $description (code: $errorCode) - URL: $failingUrl")
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            android.util.Log.d("VideoPlayer", "🔗 URL loading: $url")
                            return false
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(message: android.webkit.ConsoleMessage?): Boolean {
                            message?.let {
                                android.util.Log.d("VideoPlayer-Console",
                                    "[${it.messageLevel()}] ${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
                            }
                            return true
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress % 25 == 0) {
                                android.util.Log.d("VideoPlayer", "📊 Loading progress: $newProgress%")
                            }
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = false
                        displayZoomControls = false
                        setSupportZoom(false)
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        allowFileAccess = true
                        allowContentAccess = true
                        databaseEnabled = true

                        // Additional settings to prevent crashes
                        cacheMode = WebSettings.LOAD_DEFAULT
                        setSupportMultipleWindows(false)
                        javaScriptCanOpenWindowsAutomatically = false
                    }

                    android.util.Log.d("VideoPlayer", "WebView initialized for video ID: $videoId")
                    android.util.Log.d("VideoPlayer", "https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1")

                    // Load YouTube embedded player (simplified to prevent crashes)
                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                * { margin: 0; padding: 0; }
                                body {
                                    background-color: #000;
                                    overflow: hidden;
                                }
                                iframe {
                                    width: 100vw;
                                    height: 100vh;
                                    border: 0;
                                }
                            </style>
                        </head>
                        <body>
                            <iframe
                                src="https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1"
                                allow="accelerometer; encrypted-media; gyroscope; picture-in-picture"
                                allowfullscreen>
                            </iframe>
                        </body>
                        </html>
                    """.trimIndent()

                    loadDataWithBaseURL(
                        "https://www.youtube.com",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { webView ->
                // Force WebView to be visible
                webView.visibility = android.view.View.VISIBLE
            },
            onRelease = { webView ->
                // Proper cleanup when the composable is disposed
                webView.stopLoading()
                webView.loadUrl("about:blank")
                webView.clearHistory()
                webView.clearCache(true)
                webView.onPause()
                webView.removeAllViews()
                webView.destroy()
            }
        )
        }

        // Error overlay with reload option
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable {
                        hasError = false
                        reloadTrigger++
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Auto-reloading...",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    CircularProgressIndicator(color = White)
                }
            }
        }

        // Custom top bar with back button and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 20.dp)
                        ) {
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Video title
                Text(
                    text = videoTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White,
                    maxLines = 1
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading video...",
                        color = White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Helper function to extract YouTube video ID from various URL formats
fun extractYouTubeVideoId(url: String): String {
    android.util.Log.d("extractYouTubeVideoId", "Input URL: $url")

    val videoId = when {
        url.contains("youtu.be/") -> {
            url.substringAfter("youtu.be/")
                .substringBefore("?")
                .substringBefore("&")
                .trim()
        }
        url.contains("youtube.com/watch?v=") || url.contains("youtube.com/watch\\?v=") -> {
            val afterV = url.substringAfter("v=")
            afterV.substringBefore("&")
                .substringBefore("?")
                .substringBefore("#")
                .trim()
        }
        url.contains("youtube.com/embed/") -> {
            url.substringAfter("embed/")
                .substringBefore("?")
                .substringBefore("&")
                .trim()
        }
        // If it's already just the video ID (11 characters)
        url.length == 11 && url.matches(Regex("[a-zA-Z0-9_-]{11}")) -> {
            url.trim()
        }
        else -> {
            android.util.Log.w("extractYouTubeVideoId", "Could not extract video ID, returning original: $url")
            url.trim()
        }
    }

    android.util.Log.d("extractYouTubeVideoId", "Extracted video ID: $videoId")
    return videoId
}
