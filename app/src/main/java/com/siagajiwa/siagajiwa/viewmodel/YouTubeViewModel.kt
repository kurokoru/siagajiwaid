package com.siagajiwa.siagajiwa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwa.data.models.*
import com.siagajiwa.siagajiwa.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for YouTube API operations
 * Demonstrates how to use YouTubeRepository to fetch video data
 */
class YouTubeViewModel : ViewModel() {

    companion object {
        private const val TAG = "YouTubeViewModel"
    }

    private val repository = YouTubeRepository()

    // Search results state
    private val _searchResults = MutableStateFlow<List<YouTubeSearchResult>>(emptyList())
    val searchResults: StateFlow<List<YouTubeSearchResult>> = _searchResults.asStateFlow()

    // Video details state
    private val _videoDetails = MutableStateFlow<YouTubeVideo?>(null)
    val videoDetails: StateFlow<YouTubeVideo?> = _videoDetails.asStateFlow()

    // Videos list state
    private val _videos = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val videos: StateFlow<List<YouTubeVideo>> = _videos.asStateFlow()

    // Playlist items state
    private val _playlistItems = MutableStateFlow<List<YouTubePlaylistItem>>(emptyList())
    val playlistItems: StateFlow<List<YouTubePlaylistItem>> = _playlistItems.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Pagination token for search results
    private var nextPageToken: String? = null

    /**
     * Search for videos
     */
    fun searchVideos(
        query: String,
        maxResults: Int = 10,
        order: String = "relevance"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.searchVideos(
                query = query,
                maxResults = maxResults,
                order = order
            ).fold(
                onSuccess = { response ->
                    Log.d(TAG, "Search successful: ${response.items.size} results")
                    _searchResults.value = response.items
                    nextPageToken = response.nextPageToken
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Search failed", exception)
                    _error.value = exception.message ?: "Failed to search videos"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Load more search results (pagination)
     */
    fun loadMoreSearchResults() {
        if (nextPageToken == null || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            // Get the last search query from current results
            // In a real app, you'd store the query separately
            repository.searchVideos(
                query = "mental health", // You should store and use the actual query
                pageToken = nextPageToken
            ).fold(
                onSuccess = { response ->
                    Log.d(TAG, "Load more successful: ${response.items.size} results")
                    _searchResults.value = _searchResults.value + response.items
                    nextPageToken = response.nextPageToken
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Load more failed", exception)
                    _error.value = exception.message ?: "Failed to load more results"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Get video details by ID
     */
    fun getVideoDetails(videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getVideoDetails(videoId).fold(
                onSuccess = { video ->
                    Log.d(TAG, "Video details loaded: ${video?.snippet?.title}")
                    _videoDetails.value = video
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to get video details", exception)
                    _error.value = exception.message ?: "Failed to get video details"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Get multiple videos by IDs
     */
    fun getVideos(videoIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getVideos(videoIds).fold(
                onSuccess = { videos ->
                    Log.d(TAG, "Videos loaded: ${videos.size} videos")
                    _videos.value = videos
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to get videos", exception)
                    _error.value = exception.message ?: "Failed to get videos"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Get videos from YouTube URL
     * Useful for getting metadata for hardcoded video URLs
     */
    fun getVideosFromUrls(urls: List<String>) {
        val videoIds = urls.mapNotNull { url ->
            repository.extractVideoId(url)
        }

        if (videoIds.isNotEmpty()) {
            getVideos(videoIds)
        } else {
            _error.value = "No valid video IDs found in URLs"
        }
    }

    /**
     * Get playlist items
     */
    fun getPlaylistItems(playlistId: String, maxResults: Int = 25) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getPlaylistItems(
                playlistId = playlistId,
                maxResults = maxResults
            ).fold(
                onSuccess = { response ->
                    Log.d(TAG, "Playlist items loaded: ${response.items.size} items")
                    _playlistItems.value = response.items
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to get playlist items", exception)
                    _error.value = exception.message ?: "Failed to get playlist items"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Get popular videos
     */
    fun getPopularVideos(
        regionCode: String = "ID",
        videoCategoryId: String? = null,
        maxResults: Int = 25
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getPopularVideos(
                regionCode = regionCode,
                videoCategoryId = videoCategoryId,
                maxResults = maxResults
            ).fold(
                onSuccess = { response ->
                    Log.d(TAG, "Popular videos loaded: ${response.items.size} videos")
                    _videos.value = response.items
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to get popular videos", exception)
                    _error.value = exception.message ?: "Failed to get popular videos"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Get channel videos
     */
    fun getChannelVideos(
        channelId: String,
        maxResults: Int = 25,
        order: String = "date"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getChannelVideos(
                channelId = channelId,
                maxResults = maxResults,
                order = order
            ).fold(
                onSuccess = { response ->
                    Log.d(TAG, "Channel videos loaded: ${response.items.size} videos")
                    _searchResults.value = response.items
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to get channel videos", exception)
                    _error.value = exception.message ?: "Failed to get channel videos"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Get related videos
     */
    fun getRelatedVideos(videoId: String, maxResults: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getRelatedVideos(
                videoId = videoId,
                maxResults = maxResults
            ).fold(
                onSuccess = { response ->
                    Log.d(TAG, "Related videos loaded: ${response.items.size} videos")
                    _searchResults.value = response.items
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to get related videos", exception)
                    _error.value = exception.message ?: "Failed to get related videos"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Helper methods from repository
     */
    fun extractVideoId(url: String): String? = repository.extractVideoId(url)
    fun parseDuration(duration: String): String = repository.parseDuration(duration)
    fun formatViewCount(viewCount: String): String = repository.formatViewCount(viewCount)
}
