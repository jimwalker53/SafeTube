package com.safetube.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelDto(
    @Json(name = "kind") val kind: String?,
    @Json(name = "etag") val etag: String?,
    @Json(name = "id") val id: String?,
    @Json(name = "snippet") val snippet: ChannelSnippet?,
    @Json(name = "statistics") val statistics: ChannelStatistics?
)

@JsonClass(generateAdapter = true)
data class ChannelSnippet(
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "customUrl") val customUrl: String?,
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "thumbnails") val thumbnails: Thumbnails?,
    @Json(name = "localized") val localized: LocalizedInfo?,
    @Json(name = "country") val country: String?
)

@JsonClass(generateAdapter = true)
data class ChannelStatistics(
    @Json(name = "viewCount") val viewCount: String?,
    @Json(name = "subscriberCount") val subscriberCount: String?,
    @Json(name = "hiddenSubscriberCount") val hiddenSubscriberCount: Boolean?,
    @Json(name = "videoCount") val videoCount: String?
)
