package com.siagajiwa.siagajiwa.screens

import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.ui.theme.White

@Composable
fun AlternativeVideoPlayerScreen(
    navController: NavHostController,
    videoUrl: String,
    videoTitle: String
) {
    val context = LocalContext.current

    // Decode URL if it's encoded
    val decodedUrl = remember(videoUrl) {
        try {
            java.net.URLDecoder.decode(videoUrl, "UTF-8")
        } catch (e: Exception) {
            android.util.Log.e("AlternativePlayer", "Error decoding URL: $videoUrl", e)
            videoUrl
        }
    }

    // Decode video title
    val decodedTitle = remember(videoTitle) {
        try {
            java.net.URLDecoder.decode(videoTitle, "UTF-8")
        } catch (e: Exception) {
            videoTitle
        }
    }

    android.util.Log.d("AlternativePlayer", "Playing video: $decodedUrl")
    android.util.Log.d("AlternativePlayer", "Title: $decodedTitle")

    // Handle back button
    BackHandler {
        navController.popBackStack()
    }

    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Create ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(decodedUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    android.util.Log.d("AlternativePlayer", "Playback state: $playbackState")
                    when (playbackState) {
                        Player.STATE_READY -> {
                            isLoading = false
                            hasError = false
                        }
                        Player.STATE_ENDED -> {
                            android.util.Log.d("AlternativePlayer", "Playback ended")
                        }
                        Player.STATE_BUFFERING -> {
                            android.util.Log.d("AlternativePlayer", "Buffering...")
                        }
                        Player.STATE_IDLE -> {
                            android.util.Log.d("AlternativePlayer", "Player idle")
                        }
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("AlternativePlayer", "Player error: ${error.message}", error)
                    isLoading = false
                    hasError = true
                    errorMessage = when (error.errorCode) {
                        androidx.media3.common.PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ->
                            "Cannot load video (Bad HTTP status)"
                        androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                            "Network connection failed"
                        androidx.media3.common.PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE ->
                            "Invalid video format"
                        androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED ->
                            "Video file is corrupted"
                        else -> "Cannot play video: ${error.errorCodeName}"
                    }
                }
            })
        }
    }

    // Clean up player when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            android.util.Log.d("AlternativePlayer", "Releasing ExoPlayer")
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ExoPlayer View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        // Error overlay
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

                    Text(
                        text = "Video URL: $decodedUrl",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = White)
                    Text(
                        text = "Loading video...",
                        color = White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Custom top bar with back button and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = decodedTitle,
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = "ExoPlayer",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
