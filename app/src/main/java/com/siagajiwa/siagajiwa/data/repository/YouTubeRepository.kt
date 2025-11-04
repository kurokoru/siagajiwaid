package com.siagajiwa.siagajiwa.data.repository

import android.util.Log
import com.siagajiwa.siagajiwa.BuildConfig
import com.siagajiwa.siagajiwa.data.api.YouTubeApiService
import com.siagajiwa.siagajiwa.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for YouTube Data API v3 operations
 * Handles all YouTube API calls and error handling
 */
class YouTubeRepository {

    companion object {
        private const val TAG = "YouTubeRepository"
        private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
        private val API_KEY = BuildConfig.YOUTUBE_API_KEY
    }

    private val apiService: YouTubeApiService

    init {
        // Create OkHttp client with logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(YouTubeApiService::class.java)
    }

    /**
     * Search for videos
     */
    suspend fun searchVideos(
        query: String,
        maxResults: Int = 10,
        order: String = "relevance",
        pageToken: String? = null
    ): Result<YouTubeResponse<YouTubeSearchResult>> {
        return try {
            Log.d(TAG, "Searching videos: query=$query, maxResults=$maxResults, order=$order")
            val response = apiService.searchVideos(
                query = query,
                maxResults = maxResults,
                order = order,
                pageToken = pageToken,
                apiKey = API_KEY
            )
            handleResponse(response, "searchVideos")
        } catch (e: Exception) {
            Log.e(TAG, "Error searching videos", e)
            Result.failure(e)
        }
    }

    /**
     * Get video details by ID
     */
    suspend fun getVideoDetails(
        videoId: String
    ): Result<YouTubeVideo?> {
        return try {
            Log.d(TAG, "Getting video details: videoId=$videoId")
            val response = apiService.getVideoDetails(
                videoId = videoId,
                apiKey = API_KEY
            )
            val result = handleResponse(response, "getVideoDetails")
            result.map { it.items.firstOrNull() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting video details", e)
            Result.failure(e)
        }
    }

    /**
     * Get multiple videos by IDs
     */
    suspend fun getVideos(
        videoIds: List<String>
    ): Result<List<YouTubeVideo>> {
        return try {
            val idsString = videoIds.joinToString(",")
            Log.d(TAG, "Getting videos: videoIds=$idsString")
            val response = apiService.getVideos(
                videoIds = idsString,
                apiKey = API_KEY
            )
            val result = handleResponse(response, "getVideos")
            result.map { it.items }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting videos", e)
            Result.failure(e)
        }
    }

    /**
     * Get playlist details
     */
    suspend fun getPlaylist(
        playlistId: String
    ): Result<YouTubePlaylist?> {
        return try {
            Log.d(TAG, "Getting playlist: playlistId=$playlistId")
            val response = apiService.getPlaylist(
                playlistId = playlistId,
                apiKey = API_KEY
            )
            val result = handleResponse(response, "getPlaylist")
            result.map { it.items.firstOrNull() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting playlist", e)
            Result.failure(e)
        }
    }

    /**
     * Get playlist items (videos in a playlist)
     */
    suspend fun getPlaylistItems(
        playlistId: String,
        maxResults: Int = 25,
        pageToken: String? = null
    ): Result<YouTubeResponse<YouTubePlaylistItem>> {
        return try {
            Log.d(TAG, "Getting playlist items: playlistId=$playlistId, maxResults=$maxResults")
            val response = apiService.getPlaylistItems(
                playlistId = playlistId,
                maxResults = maxResults,
                pageToken = pageToken,
                apiKey = API_KEY
            )
            handleResponse(response, "getPlaylistItems")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting playlist items", e)
            Result.failure(e)
        }
    }

    /**
     * Get channel details
     */
    suspend fun getChannel(
        channelId: String
    ): Result<YouTubeChannel?> {
        return try {
            Log.d(TAG, "Getting channel: channelId=$channelId")
            val response = apiService.getChannel(
                channelId = channelId,
                apiKey = API_KEY
            )
            val result = handleResponse(response, "getChannel")
            result.map { it.items.firstOrNull() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting channel", e)
            Result.failure(e)
        }
    }

    /**
     * Get videos from a channel
     */
    suspend fun getChannelVideos(
        channelId: String,
        maxResults: Int = 25,
        order: String = "date",
        pageToken: String? = null
    ): Result<YouTubeResponse<YouTubeSearchResult>> {
        return try {
            Log.d(TAG, "Getting channel videos: channelId=$channelId, maxResults=$maxResults")
            val response = apiService.getChannelVideos(
                channelId = channelId,
                maxResults = maxResults,
                order = order,
                pageToken = pageToken,
                apiKey = API_KEY
            )
            handleResponse(response, "getChannelVideos")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting channel videos", e)
            Result.failure(e)
        }
    }

    /**
     * Get related videos
     */
    suspend fun getRelatedVideos(
        videoId: String,
        maxResults: Int = 10
    ): Result<YouTubeResponse<YouTubeSearchResult>> {
        return try {
            Log.d(TAG, "Getting related videos: videoId=$videoId, maxResults=$maxResults")
            val response = apiService.getRelatedVideos(
                relatedToVideoId = videoId,
                maxResults = maxResults,
                apiKey = API_KEY
            )
            handleResponse(response, "getRelatedVideos")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting related videos", e)
            Result.failure(e)
        }
    }

    /**
     * Get popular videos
     */
    suspend fun getPopularVideos(
        regionCode: String = "ID",
        videoCategoryId: String? = null,
        maxResults: Int = 25
    ): Result<YouTubeResponse<YouTubeVideo>> {
        return try {
            Log.d(TAG, "Getting popular videos: regionCode=$regionCode, categoryId=$videoCategoryId")
            val response = apiService.getPopularVideos(
                regionCode = regionCode,
                videoCategoryId = videoCategoryId,
                maxResults = maxResults,
                apiKey = API_KEY
            )
            handleResponse(response, "getPopularVideos")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting popular videos", e)
            Result.failure(e)
        }
    }

    /**
     * Helper function to extract video ID from various YouTube URL formats
     */
    fun extractVideoId(url: String): String? {
        return when {
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("v=").substringBefore("&")
            }
            url.contains("youtube.com/embed/") -> {
                url.substringAfter("embed/").substringBefore("?")
            }
            // If it's already just an ID
            url.matches(Regex("[a-zA-Z0-9_-]{11}")) -> url
            else -> null
        }
    }

    /**
     * Parse ISO 8601 duration to human-readable format
     * Example: PT4M13S -> 4:13
     */
    fun parseDuration(duration: String): String {
        return try {
            val regex = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
            val match = regex.find(duration) ?: return "0:00"

            val hours = match.groupValues[1].toIntOrNull() ?: 0
            val minutes = match.groupValues[2].toIntOrNull() ?: 0
            val seconds = match.groupValues[3].toIntOrNull() ?: 0

            when {
                hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
                minutes > 0 -> String.format("%d:%02d", minutes, seconds)
                else -> String.format("0:%02d", seconds)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing duration: $duration", e)
            "0:00"
        }
    }

    /**
     * Format view count to human-readable format
     * Example: 1234567 -> 1.2M views
     */
    fun formatViewCount(viewCount: String): String {
        return try {
            val count = viewCount.toLongOrNull() ?: return "0 views"
            when {
                count >= 1_000_000_000 -> String.format("%.1fB views", count / 1_000_000_000.0)
                count >= 1_000_000 -> String.format("%.1fM views", count / 1_000_000.0)
                count >= 1_000 -> String.format("%.1fK views", count / 1_000.0)
                else -> "$count views"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting view count: $viewCount", e)
            "0 views"
        }
    }

    /**
     * Generic response handler
     */
    private fun <T> handleResponse(
        response: Response<T>,
        operation: String
    ): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "$operation successful")
                Result.success(it)
            } ?: run {
                Log.e(TAG, "$operation returned null body")
                Result.failure(Exception("Response body is null"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "$operation failed: ${response.code()} - $errorBody")
            Result.failure(Exception("API Error: ${response.code()} - $errorBody"))
        }
    }
}
