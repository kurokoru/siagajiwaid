# YouTube Data API v3 Integration Guide

This guide explains how to use the YouTube Data API v3 integration in your Siaga Jiwa app.

## Table of Contents
- [Setup](#setup)
- [Getting a YouTube API Key](#getting-a-youtube-api-key)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [Available Features](#available-features)
- [UI Integration Examples](#ui-integration-examples)

## Setup

The YouTube API integration has been configured with the following components:

### Components Created:
1. **YouTubeModels.kt** - Data models for API responses
2. **YouTubeApiService.kt** - Retrofit API interface
3. **YouTubeRepository.kt** - Repository for API operations
4. **YouTubeViewModel.kt** - ViewModel for UI integration

### Dependencies Added:
```kotlin
// Retrofit for YouTube API
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
implementation("com.google.code.gson:gson:2.10.1")
```

## Getting a YouTube API Key

### Step 1: Go to Google Cloud Console
1. Visit [Google Cloud Console](https://console.cloud.google.com/)
2. Sign in with your Google account

### Step 2: Create or Select a Project
1. Click the project dropdown at the top
2. Click "New Project"
3. Name your project (e.g., "Siaga Jiwa App")
4. Click "Create"

### Step 3: Enable YouTube Data API v3
1. In the sidebar, go to "APIs & Services" > "Library"
2. Search for "YouTube Data API v3"
3. Click on it and press "Enable"

### Step 4: Create API Credentials
1. Go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "API Key"
3. Copy the generated API key
4. (Optional but recommended) Click "Restrict Key" and:
   - Under "API restrictions", select "Restrict key"
   - Select "YouTube Data API v3"
   - Under "Application restrictions", you can restrict by Android app
   - Save the restrictions

### Step 5: Add API Key to Your App
1. Open `.env` file in your project root
2. Replace `YOUR_YOUTUBE_API_KEY_HERE` with your actual API key:
```
YOUTUBE_API_KEY=AIzaSyD...your-actual-key-here
```

## Configuration

The API key is already configured in `build.gradle.kts`:

```kotlin
buildConfigField("String", "YOUTUBE_API_KEY", "\"${getEnvValue("YOUTUBE_API_KEY")}\"")
```

After adding your API key to `.env`, sync your Gradle files and rebuild the project.

## Usage Examples

### 1. Search for Videos

```kotlin
val viewModel: YouTubeViewModel = viewModel()

// Search for mental health videos
viewModel.searchVideos(
    query = "mental health awareness",
    maxResults = 10,
    order = "relevance" // Options: date, rating, relevance, title, viewCount
)

// Observe results
val searchResults by viewModel.searchResults.collectAsState()
```

### 2. Get Video Details

```kotlin
// Get details for a specific video ID
viewModel.getVideoDetails("dQw4w9WgXcQ")

// Observe video details
val videoDetails by viewModel.videoDetails.collectAsState()

// Access video information
videoDetails?.let { video ->
    val title = video.snippet?.title
    val description = video.snippet?.description
    val thumbnailUrl = video.snippet?.thumbnails?.high?.url
    val viewCount = video.statistics?.viewCount
    val duration = video.contentDetails?.duration
}
```

### 3. Get Videos from URLs

```kotlin
// Extract video IDs from YouTube URLs and fetch metadata
val videoUrls = listOf(
    "https://www.youtube.com/watch?v=shfd8vZj-T4",
    "https://www.youtube.com/watch?v=hanFFhhEGlk",
    "https://www.youtube.com/watch?v=E7gvUyajGqE"
)

viewModel.getVideosFromUrls(videoUrls)

// Observe results
val videos by viewModel.videos.collectAsState()
```

### 4. Get Playlist Items

```kotlin
// Get all videos in a playlist
viewModel.getPlaylistItems(
    playlistId = "PLxxxxxxxxxxxxxx",
    maxResults = 50
)

// Observe playlist items
val playlistItems by viewModel.playlistItems.collectAsState()
```

### 5. Get Popular Videos

```kotlin
// Get trending videos in Indonesia
viewModel.getPopularVideos(
    regionCode = "ID",
    maxResults = 25
)

// Get popular videos in a specific category
// Category IDs: 22 = People & Blogs, 24 = Entertainment, etc.
viewModel.getPopularVideos(
    regionCode = "ID",
    videoCategoryId = "22",
    maxResults = 25
)
```

### 6. Get Channel Videos

```kotlin
// Get videos from a specific channel
viewModel.getChannelVideos(
    channelId = "UCxxxxxxxxxxxxxx",
    maxResults = 25,
    order = "date" // Latest videos first
)
```

### 7. Get Related Videos

```kotlin
// Get videos related to a specific video
viewModel.getRelatedVideos(
    videoId = "dQw4w9WgXcQ",
    maxResults = 10
)
```

## Available Features

### YouTubeRepository Methods:

| Method | Description |
|--------|-------------|
| `searchVideos()` | Search for videos by query |
| `getVideoDetails()` | Get detailed info for one video |
| `getVideos()` | Get detailed info for multiple videos |
| `getPlaylist()` | Get playlist metadata |
| `getPlaylistItems()` | Get videos in a playlist |
| `getChannel()` | Get channel information |
| `getChannelVideos()` | Get videos from a channel |
| `getRelatedVideos()` | Get videos related to a video |
| `getPopularVideos()` | Get trending videos |
| `extractVideoId()` | Extract video ID from URL |
| `parseDuration()` | Convert ISO 8601 duration to readable format |
| `formatViewCount()` | Format view count (e.g., 1.2M views) |

## UI Integration Examples

### Example 1: Display Video with Metadata

```kotlin
@Composable
fun VideoCardWithMetadata(
    videoId: String,
    viewModel: YouTubeViewModel = viewModel()
) {
    val videoDetails by viewModel.videoDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(videoId) {
        viewModel.getVideoDetails(videoId)
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        videoDetails?.let { video ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Thumbnail
                AsyncImage(
                    model = video.snippet?.thumbnails?.high?.url,
                    contentDescription = video.snippet?.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = video.snippet?.title ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Channel and stats
                Row {
                    Text(
                        text = video.snippet?.channelTitle ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = viewModel.formatViewCount(
                            video.statistics?.viewCount ?: "0"
                        ),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = video.snippet?.description ?: "",
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
```

### Example 2: Video Search Screen

```kotlin
@Composable
fun VideoSearchScreen(
    viewModel: YouTubeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search videos...") },
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.searchVideos(searchQuery)
                }) {
                    Icon(Icons.Default.Search, "Search")
                }
            }
        )

        // Results
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(searchResults) { result ->
                    VideoSearchResultCard(
                        result = result,
                        onClick = {
                            // Navigate to video player
                            result.id.videoId?.let { videoId ->
                                // Navigate to VideoPlayerScreen
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VideoSearchResultCard(
    result: YouTubeSearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Thumbnail
            AsyncImage(
                model = result.snippet.thumbnails.medium?.url,
                contentDescription = result.snippet.title,
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column {
                Text(
                    text = result.snippet.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = result.snippet.channelTitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = result.snippet.description,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
```

### Example 3: Update Existing VideoScreen with API Data

```kotlin
// Instead of hardcoded video data, fetch from YouTube API
@Composable
fun VideoScreen(
    navController: NavHostController,
    viewModel: YouTubeViewModel = viewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        // Get metadata for your hardcoded video URLs
        val videoUrls = listOf(
            "https://www.youtube.com/watch?v=shfd8vZj-T4",
            "https://www.youtube.com/watch?v=hanFFhhEGlk",
            "https://www.youtube.com/watch?v=E7gvUyajGqE"
        )
        viewModel.getVideosFromUrls(videoUrls)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn {
                items(videos) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            val encodedUrl = URLEncoder.encode(
                                "https://www.youtube.com/watch?v=${video.id}",
                                StandardCharsets.UTF_8.toString()
                            )
                            val encodedTitle = URLEncoder.encode(
                                video.snippet?.title ?: "",
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate(
                                "VideoPlayerScreen/$encodedUrl/$encodedTitle"
                            )
                        }
                    )
                }
            }
        }
    }
}
```

## API Quota Information

YouTube Data API v3 has quota limits:
- **Default quota**: 10,000 units per day
- **Search**: 100 units per request
- **Videos list**: 1 unit per request
- **Playlist items**: 1 unit per request

### Tips to manage quota:
1. Cache results when possible
2. Use `maxResults` parameter wisely
3. Request only the parts you need (`snippet`, `statistics`, `contentDetails`)
4. Consider implementing pagination carefully

## Error Handling

```kotlin
val error by viewModel.error.collectAsState()

error?.let { errorMessage ->
    AlertDialog(
        onDismissRequest = { viewModel.clearError() },
        title = { Text("Error") },
        text = { Text(errorMessage) },
        confirmButton = {
            Button(onClick = { viewModel.clearError() }) {
                Text("OK")
            }
        }
    )
}
```

## Testing

You can test the API without UI using the repository directly:

```kotlin
// In a test or temporary function
lifecycleScope.launch {
    val repository = YouTubeRepository()

    // Test search
    repository.searchVideos("mental health").fold(
        onSuccess = { response ->
            Log.d("Test", "Found ${response.items.size} videos")
        },
        onFailure = { error ->
            Log.e("Test", "Error: ${error.message}")
        }
    )
}
```

## Resources

- [YouTube Data API v3 Documentation](https://developers.google.com/youtube/v3/docs)
- [API Reference](https://developers.google.com/youtube/v3/docs/videos)
- [Quota Calculator](https://developers.google.com/youtube/v3/determine_quota_cost)

## Support

If you encounter issues:
1. Check that your API key is valid and properly configured
2. Ensure YouTube Data API v3 is enabled in Google Cloud Console
3. Verify you haven't exceeded your daily quota
4. Check the Logcat for detailed error messages (tagged with "YouTubeRepository" or "YouTubeViewModel")
