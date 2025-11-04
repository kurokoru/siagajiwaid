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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
fun NativeVideoPlayerScreen(
    navController: NavHostController,
    videoUrl: String,
    videoTitle: String
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
//    val testUrl  = "https://www.youtube.com/watch?v=shfd8vZj-T4"
    val testUrl = "https://www.youtube.com/watch?v=hanFFhhEGlk"
    // Decode URL if it's encoded
    val decodedUrl = remember(testUrl) {
        try {
            java.net.URLDecoder.decode(testUrl, "UTF-8")
        } catch (e: Exception) {
            android.util.Log.e("NativeVideoPlayer", "❌ Error decoding URL: $testUrl", e)
            testUrl
        }
    }

    // Extract YouTube video ID from URL
    val videoId = remember(decodedUrl) {
        val id = extractYouTubeVideoId(decodedUrl)
        android.util.Log.d("NativeVideoPlayer", "📹 Original URL: $testUrl")
        android.util.Log.d("NativeVideoPlayer", "🔓 Decoded URL: $decodedUrl")
        android.util.Log.d("NativeVideoPlayer", "🎬 Extracted Video ID: $id")
        id
    }

    // Decode video title
    val decodedTitle = remember(videoTitle) {
        try {
            java.net.URLDecoder.decode(videoTitle, "UTF-8")
        } catch (e: Exception) {
            videoTitle
        }
    }

    // Handle back button
    BackHandler {
        navController.popBackStack()
    }

    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorCode by remember { mutableIntStateOf(0) }

    // Error messages based on YouTube error codes
    val errorMessage = remember(errorCode) {
        when (errorCode) {
            2 -> "Invalid video ID"
            5 -> "HTML5 player error"
            100 -> "Video not found or private"
            101, 150 -> "Video owner doesn't allow embedding"
            152 -> "Video owner doesn't allow embedding"
            153 -> "Video owner doesn't allow embedding (Referrer policy issue)"
            else -> if (errorCode != 0) "Cannot play video (Error: $errorCode)" else ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // YouTube Player using WebView with IFrame API
        AndroidView(
            factory = { ctx ->
                android.util.Log.d("NativeVideoPlayer", "🏗️ Creating WebView for video ID: $videoId")

                WebView(ctx).apply {
                    // Add JavaScript interface to receive error callbacks
                    addJavascriptInterface(object {
                        @android.webkit.JavascriptInterface
                        fun onPlayerError(code: Int) {
                            android.util.Log.e("NativeVideoPlayer", "❌ Player error from JS: $code")
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                errorCode = code
                                hasError = true
                                isLoading = false
                            }
                        }

                        @android.webkit.JavascriptInterface
                        fun onPlayerReady() {
                            android.util.Log.d("NativeVideoPlayer", "✅ Player ready from JS")
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                isLoading = false
                            }
                        }
                    }, "AndroidInterface")

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            android.util.Log.d("NativeVideoPlayer", "✅ Page loaded: $url")
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            android.util.Log.e("NativeVideoPlayer", "❌ Error loading: $description")
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(message: android.webkit.ConsoleMessage?): Boolean {
                            message?.let {
                                android.util.Log.d("NativeVideoPlayer-Console",
                                    "[${it.messageLevel()}] ${it.message()}")
                            }
                            return true
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        allowFileAccess = true
                        allowContentAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                    }

                    // Enhanced HTML with proper YouTube IFrame API implementation
                    // Adding referrer policy meta tag to fix error 153
                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                            <meta name="referrer" content="strict-origin-when-cross-origin">
                            <style>
                                * { margin: 0; padding: 0; }
                                html, body {
                                    height: 100%;
                                    width: 100%;
                                    background-color: #000;
                                    overflow: hidden;
                                }
                                #player-container {
                                    position: absolute;
                                    top: 0;
                                    left: 0;
                                    width: 100%;
                                    height: 100%;
                                }
                                #player {
                                    width: 100%;
                                    height: 100%;
                                }
                            </style>
                        </head>
                        <body>
                            <div id="player-container">
                                <div id="player"></div>
                            </div>

                            <script>
                                // YouTube IFrame API
                                var tag = document.createElement('script');
                                tag.src = "https://www.youtube.com/iframe_api";
                                var firstScriptTag = document.getElementsByTagName('script')[0];
                                firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                                var player;
                                function onYouTubeIframeAPIReady() {
                                    console.log('YouTube IFrame API Ready');
                                    player = new YT.Player('player', {
                                        height: '100%',
                                        width: '100%',
                                        videoId: '$videoId',
                                        playerVars: {
                                            'playsinline': 1,
                                            'autoplay': 0,
                                            'controls': 1,
                                            'rel': 0,
                                            'modestbranding': 1,
                                            'iv_load_policy': 3
                                        },
                                        events: {
                                            'onReady': onPlayerReady,
                                            'onStateChange': onPlayerStateChange,
                                            'onError': onPlayerError
                                        }
                                    });
                                }

                                function onPlayerReady(event) {
                                    console.log('Player ready for video: $videoId');
                                    if (typeof AndroidInterface !== 'undefined') {
                                        AndroidInterface.onPlayerReady();
                                    }
                                }

                                function onPlayerStateChange(event) {
                                    console.log('Player state: ' + event.data);
                                }

                                function onPlayerError(event) {
                                    console.error('Player error: ' + event.data);
                                    if (typeof AndroidInterface !== 'undefined') {
                                        AndroidInterface.onPlayerError(event.data);
                                    }
                                }
                            </script>
                        </body>
                        </html>
                    """.trimIndent()

                    android.util.Log.d("NativeVideoPlayer", "Loading HTML for video ID: $videoId")
                    // Use app package name as base URL to provide proper referrer context
                    // This fixes YouTube error 153 (referrer policy issue)
                    val baseUrl = "https://${ctx.packageName}"
                    android.util.Log.d("NativeVideoPlayer", "Using base URL: $baseUrl")
                    loadDataWithBaseURL(
                        baseUrl,
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .align(Alignment.Center),
            onRelease = { webView ->
                android.util.Log.d("NativeVideoPlayer", "🗑️ Releasing WebView")
                webView.stopLoading()
                webView.loadUrl("about:blank")
                webView.destroy()
            }
        )

        // Error overlay with option to open in YouTube
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Error",
                        tint = White.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = errorMessage,
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Open in YouTube button
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFF0000),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                // Open YouTube app or browser
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(decodedUrl)
                                )
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Open in YouTube",
                            color = White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Loading indicator
        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = White)
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

                // Title
                Text(
                    text = decodedTitle,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
