package com.siagajiwa.siagajiwa.data.models

import com.google.gson.annotations.SerializedName

// YouTube Data API v3 Response Models

/**
 * Main response wrapper for YouTube API calls
 */
data class YouTubeResponse<T>(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("nextPageToken")
    val nextPageToken: String?,

    @SerializedName("prevPageToken")
    val prevPageToken: String?,

    @SerializedName("pageInfo")
    val pageInfo: PageInfo,

    @SerializedName("items")
    val items: List<T>
)

data class PageInfo(
    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("resultsPerPage")
    val resultsPerPage: Int
)

/**
 * Video resource
 */
data class YouTubeVideo(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("snippet")
    val snippet: VideoSnippet?,

    @SerializedName("contentDetails")
    val contentDetails: VideoContentDetails?,

    @SerializedName("statistics")
    val statistics: VideoStatistics?
)

data class VideoSnippet(
    @SerializedName("publishedAt")
    val publishedAt: String,

    @SerializedName("channelId")
    val channelId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,

    @SerializedName("channelTitle")
    val channelTitle: String,

    @SerializedName("tags")
    val tags: List<String>?,

    @SerializedName("categoryId")
    val categoryId: String,

    @SerializedName("liveBroadcastContent")
    val liveBroadcastContent: String,

    @SerializedName("defaultLanguage")
    val defaultLanguage: String?,

    @SerializedName("localized")
    val localized: Localized?
)

data class VideoContentDetails(
    @SerializedName("duration")
    val duration: String,

    @SerializedName("dimension")
    val dimension: String,

    @SerializedName("definition")
    val definition: String,

    @SerializedName("caption")
    val caption: String,

    @SerializedName("licensedContent")
    val licensedContent: Boolean,

    @SerializedName("projection")
    val projection: String
)

data class VideoStatistics(
    @SerializedName("viewCount")
    val viewCount: String,

    @SerializedName("likeCount")
    val likeCount: String?,

    @SerializedName("dislikeCount")
    val dislikeCount: String?,

    @SerializedName("favoriteCount")
    val favoriteCount: String,

    @SerializedName("commentCount")
    val commentCount: String?
)

data class Thumbnails(
    @SerializedName("default")
    val default: Thumbnail,

    @SerializedName("medium")
    val medium: Thumbnail?,

    @SerializedName("high")
    val high: Thumbnail?,

    @SerializedName("standard")
    val standard: Thumbnail?,

    @SerializedName("maxres")
    val maxres: Thumbnail?
)

data class Thumbnail(
    @SerializedName("url")
    val url: String,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int
)

data class Localized(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String
)

/**
 * Search result
 */
data class YouTubeSearchResult(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("id")
    val id: SearchResultId,

    @SerializedName("snippet")
    val snippet: VideoSnippet
)

data class SearchResultId(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("videoId")
    val videoId: String?
)

/**
 * Playlist resource
 */
data class YouTubePlaylist(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("snippet")
    val snippet: PlaylistSnippet?,

    @SerializedName("contentDetails")
    val contentDetails: PlaylistContentDetails?
)

data class PlaylistSnippet(
    @SerializedName("publishedAt")
    val publishedAt: String,

    @SerializedName("channelId")
    val channelId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,

    @SerializedName("channelTitle")
    val channelTitle: String,

    @SerializedName("localized")
    val localized: Localized?
)

data class PlaylistContentDetails(
    @SerializedName("itemCount")
    val itemCount: Int
)

/**
 * Playlist item resource
 */
data class YouTubePlaylistItem(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("snippet")
    val snippet: PlaylistItemSnippet?,

    @SerializedName("contentDetails")
    val contentDetails: PlaylistItemContentDetails?
)

data class PlaylistItemSnippet(
    @SerializedName("publishedAt")
    val publishedAt: String,

    @SerializedName("channelId")
    val channelId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,

    @SerializedName("channelTitle")
    val channelTitle: String,

    @SerializedName("playlistId")
    val playlistId: String,

    @SerializedName("position")
    val position: Int,

    @SerializedName("resourceId")
    val resourceId: ResourceId
)

data class PlaylistItemContentDetails(
    @SerializedName("videoId")
    val videoId: String,

    @SerializedName("videoPublishedAt")
    val videoPublishedAt: String?
)

data class ResourceId(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("videoId")
    val videoId: String
)

/**
 * Channel resource
 */
data class YouTubeChannel(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("snippet")
    val snippet: ChannelSnippet?,

    @SerializedName("statistics")
    val statistics: ChannelStatistics?
)

data class ChannelSnippet(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("customUrl")
    val customUrl: String?,

    @SerializedName("publishedAt")
    val publishedAt: String,

    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,

    @SerializedName("localized")
    val localized: Localized?
)

data class ChannelStatistics(
    @SerializedName("viewCount")
    val viewCount: String,

    @SerializedName("subscriberCount")
    val subscriberCount: String,

    @SerializedName("hiddenSubscriberCount")
    val hiddenSubscriberCount: Boolean,

    @SerializedName("videoCount")
    val videoCount: String
)
