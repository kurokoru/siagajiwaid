package com.siagajiwa.siagajiwa.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.data.models.YouTubeVideo
import com.siagajiwa.siagajiwa.viewmodel.YouTubeViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.PurpleNetral
import com.siagajiwa.siagajiwa.ui.theme.White

data class VideoItem(
    val title: String,
    val channel: String,
    val youtubeUrl: String,
    val thumbnailRes: Int,
    // YouTube API metadata
    val youtubeData: YouTubeVideo? = null
)

@Composable
fun VideoScreen(
    navController: NavHostController,
    viewModel: YouTubeViewModel = viewModel()
) {
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) }

    // Hardcoded video URLs
    val videoUrls = listOf(
        "https://www.youtube.com/watch?v=shfd8vZj-T4",
        "https://www.youtube.com/watch?v=hanFFhhEGlk",
        "https://www.youtube.com/watch?v=E7gvUyajGqE"
    )

    // Fallback video data (used if API fails or while loading)
    val fallbackVideos = listOf(
        VideoItem(
            title = "Relaksasi Otot Progresif",
            channel = "Siaga Jiwa",
            youtubeUrl = videoUrls[0],
            thumbnailRes = R.drawable.video_thumbnail_1
        ),
        VideoItem(
            title = "Relaksasi Napas Dalam",
            channel = "Siaga Jiwa",
            youtubeUrl = videoUrls[1],
            thumbnailRes = R.drawable.video_thumbnail_2
        ),
        VideoItem(
            title = "Terapi Imajinasi Terpadu",
            channel = "Siaga Jiwa",
            youtubeUrl = videoUrls[2],
            thumbnailRes = R.drawable.video_thumbnail_3
        )
    )

    // Fetch video metadata from YouTube API
    val youtubeVideos by viewModel.videos.collectAsState()
    val isLoadingYouTube by viewModel.isLoading.collectAsState()

    // Fetch video data on first composition
    LaunchedEffect(Unit) {
        viewModel.getVideosFromUrls(videoUrls)
    }

    // Merge YouTube data with fallback data
    val videos = remember(youtubeVideos) {
        if (youtubeVideos.isNotEmpty()) {
            fallbackVideos.mapIndexed { index, fallback ->
                val youtubeData = youtubeVideos.getOrNull(index)
                fallback.copy(
                    title = youtubeData?.snippet?.title ?: fallback.title,
                    channel = youtubeData?.snippet?.channelTitle ?: fallback.channel,
                    youtubeData = youtubeData
                )
            }
        } else {
            fallbackVideos
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Space
            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Bar
            NavigationBar(
                onBackClick = { navController.popBackStack() }
            )

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White),
                contentPadding = PaddingValues(
                    start = 15.dp,
                    end = 15.dp,
                    top = 24.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = videos.size,
                    key = { index -> videos[index].youtubeUrl }
                ) { index ->
                    VideoCard(
                        video = videos[index],
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = selectedBottomNavIndex,
            onItemSelected = { selectedBottomNavIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

@Composable
fun NavigationBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 20.dp)
                ) { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Back",
                tint = DarkLight,
                modifier = Modifier.size(24.dp)
            )
        }

        // Title
        Text(
            text = "Video Tutorial",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkLight,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VideoCard(
    video: VideoItem,
    navController: NavHostController,
    viewModel: YouTubeViewModel = viewModel()
) {
    // Extract YouTube video ID from URL
    val videoId = remember(video.youtubeUrl) {
        extractYouTubeVideoId(video.youtubeUrl)
    }

    // Get YouTube thumbnail URL or use local fallback
    val thumbnailUrl = video.youtubeData?.snippet?.thumbnails?.high?.url
        ?: video.youtubeData?.snippet?.thumbnails?.medium?.url

    // Get additional metadata
    val viewCount = video.youtubeData?.statistics?.viewCount?.let {
        viewModel.formatViewCount(it)
    }
    val duration = video.youtubeData?.contentDetails?.duration?.let {
        viewModel.parseDuration(it)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .clickable {
                // Navigate to full-screen video player
                val encodedUrl = java.net.URLEncoder.encode(video.youtubeUrl, "UTF-8")
                val encodedTitle = java.net.URLEncoder.encode(video.title, "UTF-8")
                navController.navigate("VideoPlayerScreen/$encodedUrl/$encodedTitle")
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Show Thumbnail
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = video.thumbnailRes),
                    error = painterResource(id = video.thumbnailRes)
                )
            } else {
                Image(
                    painter = painterResource(id = video.thumbnailRes),
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Overlay gradient for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            // Duration badge (top right)
            duration?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }
            }

            // Play button
            Box(
                modifier = Modifier
                    .size(61.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Play",
                    tint = White,
                    modifier = Modifier.size(61.dp)
                )
            }

            // Video info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 27.dp, bottom = 24.dp, end = 27.dp)
            ) {
                Text(
                    text = video.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleNetral,
                    lineHeight = 28.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = video.channel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = PurpleNetral,
                        lineHeight = 28.sp
                    )
                    // Show view count if available
                    viewCount?.let {
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = PurpleNetral
                        )
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = PurpleNetral,
                            lineHeight = 28.sp
                        )
                    }
                }
            }
        }
    }
}

