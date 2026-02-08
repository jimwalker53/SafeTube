package com.safetube.data.remote.api

import com.safetube.data.remote.dto.ChannelDto
import com.safetube.data.remote.dto.SearchResultDto
import com.safetube.data.remote.dto.SubscribeRequest
import com.safetube.data.remote.dto.SubscriptionDto
import com.safetube.data.remote.dto.VideoDto
import com.safetube.data.remote.dto.YouTubeListResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface YouTubeApiService {

    /**
     * Search for videos on YouTube.
     */
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 20,
        @Query("pageToken") pageToken: String? = null,
        @Query("order") order: String = "relevance",
        @Query("videoDuration") videoDuration: String? = null,
        @Query("publishedAfter") publishedAfter: String? = null
    ): Response<YouTubeListResponse<SearchResultDto>>

    /**
     * Get videos by their IDs.
     */
    @GET("videos")
    suspend fun getVideos(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("id") ids: String,
        @Query("maxResults") maxResults: Int = 50
    ): Response<YouTubeListResponse<VideoDto>>

    /**
     * Get popular/trending videos.
     */
    @GET("videos")
    suspend fun getPopularVideos(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") regionCode: String = "US",
        @Query("maxResults") maxResults: Int = 20,
        @Query("pageToken") pageToken: String? = null,
        @Query("videoCategoryId") categoryId: String? = null
    ): Response<YouTubeListResponse<VideoDto>>

    /**
     * Get related videos for a specific video.
     */
    @GET("search")
    suspend fun getRelatedVideos(
        @Query("part") part: String = "snippet",
        @Query("relatedToVideoId") videoId: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 15
    ): Response<YouTubeListResponse<SearchResultDto>>

    /**
     * Search for videos within a specific channel.
     */
    @GET("search")
    suspend fun getChannelVideos(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("type") type: String = "video",
        @Query("order") order: String = "date",
        @Query("maxResults") maxResults: Int = 20,
        @Query("pageToken") pageToken: String? = null
    ): Response<YouTubeListResponse<SearchResultDto>>

    /**
     * Get channel details.
     */
    @GET("channels")
    suspend fun getChannels(
        @Query("part") part: String = "snippet,statistics",
        @Query("id") ids: String
    ): Response<YouTubeListResponse<ChannelDto>>

    /**
     * Get user subscriptions (requires OAuth).
     */
    @GET("subscriptions")
    suspend fun getSubscriptions(
        @Header("Authorization") authToken: String,
        @Query("part") part: String = "snippet,contentDetails",
        @Query("mine") mine: Boolean = true,
        @Query("maxResults") maxResults: Int = 50,
        @Query("pageToken") pageToken: String? = null,
        @Query("order") order: String = "alphabetical"
    ): Response<YouTubeListResponse<SubscriptionDto>>

    /**
     * Subscribe to a channel (requires OAuth).
     */
    @POST("subscriptions")
    suspend fun subscribe(
        @Header("Authorization") authToken: String,
        @Query("part") part: String = "snippet",
        @Body request: SubscribeRequest
    ): Response<SubscriptionDto>

    /**
     * Unsubscribe from a channel (requires OAuth).
     */
    @DELETE("subscriptions")
    suspend fun unsubscribe(
        @Header("Authorization") authToken: String,
        @Query("id") subscriptionId: String
    ): Response<Unit>

    /**
     * Rate a video (like/dislike) (requires OAuth).
     */
    @POST("videos/rate")
    suspend fun rateVideo(
        @Header("Authorization") authToken: String,
        @Query("id") videoId: String,
        @Query("rating") rating: String // "like", "dislike", or "none"
    ): Response<Unit>

    /**
     * Get the user's rating for videos (requires OAuth).
     */
    @GET("videos/getRating")
    suspend fun getVideoRating(
        @Header("Authorization") authToken: String,
        @Query("id") videoIds: String
    ): Response<VideoRatingResponse>
}

/**
 * Response for video rating query.
 */
@JsonClass(generateAdapter = true)
data class VideoRatingResponse(
    @Json(name = "items") val items: List<VideoRating>?
)

@JsonClass(generateAdapter = true)
data class VideoRating(
    @Json(name = "videoId") val videoId: String?,
    @Json(name = "rating") val rating: String? // "like", "dislike", "none", "unspecified"
)
