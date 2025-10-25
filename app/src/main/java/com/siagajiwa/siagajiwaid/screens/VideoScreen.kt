package com.siagajiwa.siagajiwaid.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.R
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.PurpleNetral
import com.siagajiwa.siagajiwaid.ui.theme.White

data class VideoItem(
    val title: String,
    val channel: String,
    val youtubeUrl: String,
    val thumbnailRes: Int
)

@Composable
fun VideoScreen(navController: NavHostController) {
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) }

    val videos = listOf(
        VideoItem(
            title = "Relaksasi Otot Progresif",
            channel = "Siaga Jiwa",
            youtubeUrl = "https://www.youtube.com/watch?v=shfd8vZj-T4",
            thumbnailRes = R.drawable.video_thumbnail_1
        ),
        VideoItem(
            title = "Relaksasi Napas Dalam",
            channel = "Siaga Jiwa",
            youtubeUrl = "https://www.youtube.com/watch?v=hanFFhhEGlk",
            thumbnailRes = R.drawable.video_thumbnail_2
        ),
        VideoItem(
            title = "Terapi Imajinasi Terpadu",
            channel = "Siaga Jiwa",
            youtubeUrl = "https://www.youtube.com/watch?v=E7gvUyajGqE",
            thumbnailRes = R.drawable.video_thumbnail_3
        )
    )

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
                verticalArrangement = Arrangement.spacedBy(34.dp)
            ) {
                items(videos.size) { index ->
                    VideoCard(
                        video = videos[index],
                        onClick = {
                            val encodedUrl = URLEncoder.encode(videos[index].youtubeUrl, StandardCharsets.UTF_8.toString())
                            val encodedTitle = URLEncoder.encode(videos[index].title, StandardCharsets.UTF_8.toString())
                            navController.navigate("VideoPlayerScreen/$encodedUrl/$encodedTitle")
                        }
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
            .height(53.dp)
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
                modifier = Modifier.size(16.dp)
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Thumbnail
            Image(
                painter = painterResource(id = video.thumbnailRes),
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            // Overlay gradient for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

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
                    .padding(start = 27.dp, bottom = 24.dp)
            ) {
                Text(
                    text = video.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleNetral,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.channel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = PurpleNetral,
                    lineHeight = 28.sp
                )
            }
        }
    }
}
