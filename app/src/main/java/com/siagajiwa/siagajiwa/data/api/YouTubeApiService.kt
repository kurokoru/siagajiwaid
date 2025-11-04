package com.siagajiwa.siagajiwa.data.api

import com.siagajiwa.siagajiwa.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * YouTube Data API v3 Service Interface
 * Base URL: https://www.googleapis.com/youtube/v3/
 *
 * API Documentation: https://developers.google.com/youtube/v3/docs
 */
interface YouTubeApiService {

    /**
     * Search for videos
     *
     * @param query Search query term
     * @param part Comma-separated list of resource properties (snippet, contentDetails, statistics)
     * @param type Resource type (video, channel, playlist)
     * @param maxResults Maximum number of results (1-50, default 5)
     * @param order Sort order (date, rating, relevance, title, videoCount, viewCount)
     * @param pageToken Token for pagination
     * @param apiKey YouTube Data API key
     */
    @GET("search")
    suspend fun searchVideos(
        @Query("q") query: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("order") order: String = "relevance",
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeSearchResult>>

    /**
     * Get video details by ID
     *
     * @param videoId Comma-separated list of video IDs
     * @param part Comma-separated list of resource properties
     * @param apiKey YouTube Data API key
     */
    @GET("videos")
    suspend fun getVideoDetails(
        @Query("id") videoId: String,
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeVideo>>

    /**
     * Get multiple videos by IDs
     *
     * @param videoIds Comma-separated list of video IDs
     * @param part Comma-separated list of resource properties
     * @param apiKey YouTube Data API key
     */
    @GET("videos")
    suspend fun getVideos(
        @Query("id") videoIds: String,
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeVideo>>

    /**
     * Get playlist details by ID
     *
     * @param playlistId Playlist ID
     * @param part Comma-separated list of resource properties
     * @param apiKey YouTube Data API key
     */
    @GET("playlists")
    suspend fun getPlaylist(
        @Query("id") playlistId: String,
        @Query("part") part: String = "snippet,contentDetails",
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubePlaylist>>

    /**
     * Get playlist items (videos in a playlist)
     *
     * @param playlistId Playlist ID
     * @param part Comma-separated list of resource properties
     * @param maxResults Maximum number of results (1-50, default 25)
     * @param pageToken Token for pagination
     * @param apiKey YouTube Data API key
     */
    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("playlistId") playlistId: String,
        @Query("part") part: String = "snippet,contentDetails",
        @Query("maxResults") maxResults: Int = 25,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubePlaylistItem>>

    /**
     * Get channel details by ID
     *
     * @param channelId Channel ID
     * @param part Comma-separated list of resource properties
     * @param apiKey YouTube Data API key
     */
    @GET("channels")
    suspend fun getChannel(
        @Query("id") channelId: String,
        @Query("part") part: String = "snippet,statistics",
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeChannel>>

    /**
     * Search videos by channel
     *
     * @param channelId Channel ID
     * @param part Comma-separated list of resource properties
     * @param order Sort order
     * @param maxResults Maximum number of results
     * @param pageToken Token for pagination
     * @param apiKey YouTube Data API key
     */
    @GET("search")
    suspend fun getChannelVideos(
        @Query("channelId") channelId: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("order") order: String = "date",
        @Query("maxResults") maxResults: Int = 25,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeSearchResult>>

    /**
     * Get related videos
     *
     * @param relatedToVideoId Video ID to find related videos for
     * @param part Comma-separated list of resource properties
     * @param type Resource type
     * @param maxResults Maximum number of results
     * @param apiKey YouTube Data API key
     */
    @GET("search")
    suspend fun getRelatedVideos(
        @Query("relatedToVideoId") relatedToVideoId: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeSearchResult>>

    /**
     * Get popular videos by category
     *
     * @param chart Chart parameter (mostPopular)
     * @param part Comma-separated list of resource properties
     * @param regionCode Region code (e.g., US, GB, ID)
     * @param videoCategoryId Video category ID
     * @param maxResults Maximum number of results
     * @param apiKey YouTube Data API key
     */
    @GET("videos")
    suspend fun getPopularVideos(
        @Query("chart") chart: String = "mostPopular",
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("regionCode") regionCode: String = "ID",
        @Query("videoCategoryId") videoCategoryId: String? = null,
        @Query("maxResults") maxResults: Int = 25,
        @Query("key") apiKey: String
    ): Response<YouTubeResponse<YouTubeVideo>>
}
