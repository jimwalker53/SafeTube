package com.safetube.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoDto(
    @Json(name = "kind") val kind: String?,
    @Json(name = "etag") val etag: String?,
    @Json(name = "id") val id: String?,
    @Json(name = "snippet") val snippet: VideoSnippet?,
    @Json(name = "contentDetails") val contentDetails: ContentDetails?,
    @Json(name = "statistics") val statistics: VideoStatistics?
)

@JsonClass(generateAdapter = true)
data class VideoSnippet(
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "channelId") val channelId: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "thumbnails") val thumbnails: Thumbnails?,
    @Json(name = "channelTitle") val channelTitle: String?,
    @Json(name = "tags") val tags: List<String>?,
    @Json(name = "categoryId") val categoryId: String?,
    @Json(name = "liveBroadcastContent") val liveBroadcastContent: String?,
    @Json(name = "localized") val localized: LocalizedInfo?
)

@JsonClass(generateAdapter = true)
data class Thumbnails(
    @Json(name = "default") val default: Thumbnail?,
    @Json(name = "medium") val medium: Thumbnail?,
    @Json(name = "high") val high: Thumbnail?,
    @Json(name = "standard") val standard: Thumbnail?,
    @Json(name = "maxres") val maxres: Thumbnail?
)

@JsonClass(generateAdapter = true)
data class Thumbnail(
    @Json(name = "url") val url: String?,
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?
)

@JsonClass(generateAdapter = true)
data class LocalizedInfo(
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?
)

@JsonClass(generateAdapter = true)
data class ContentDetails(
    @Json(name = "duration") val duration: String?,
    @Json(name = "dimension") val dimension: String?,
    @Json(name = "definition") val definition: String?,
    @Json(name = "caption") val caption: String?,
    @Json(name = "licensedContent") val licensedContent: Boolean?,
    @Json(name = "projection") val projection: String?
)

@JsonClass(generateAdapter = true)
data class VideoStatistics(
    @Json(name = "viewCount") val viewCount: String?,
    @Json(name = "likeCount") val likeCount: String?,
    @Json(name = "favoriteCount") val favoriteCount: String?,
    @Json(name = "commentCount") val commentCount: String?
)
