package com.safetube.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubscriptionDto(
    @Json(name = "kind") val kind: String?,
    @Json(name = "etag") val etag: String?,
    @Json(name = "id") val id: String?,
    @Json(name = "snippet") val snippet: SubscriptionSnippet?,
    @Json(name = "contentDetails") val contentDetails: SubscriptionContentDetails?
)

@JsonClass(generateAdapter = true)
data class SubscriptionSnippet(
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "resourceId") val resourceId: ResourceId?,
    @Json(name = "channelId") val channelId: String?,
    @Json(name = "thumbnails") val thumbnails: Thumbnails?
)

@JsonClass(generateAdapter = true)
data class ResourceId(
    @Json(name = "kind") val kind: String?,
    @Json(name = "channelId") val channelId: String?
)

@JsonClass(generateAdapter = true)
data class SubscriptionContentDetails(
    @Json(name = "totalItemCount") val totalItemCount: Int?,
    @Json(name = "newItemCount") val newItemCount: Int?,
    @Json(name = "activityType") val activityType: String?
)

/**
 * Request body for subscribing to a channel.
 */
@JsonClass(generateAdapter = true)
data class SubscribeRequest(
    @Json(name = "snippet") val snippet: SubscribeSnippet
)

@JsonClass(generateAdapter = true)
data class SubscribeSnippet(
    @Json(name = "resourceId") val resourceId: SubscribeResourceId
)

@JsonClass(generateAdapter = true)
data class SubscribeResourceId(
    @Json(name = "kind") val kind: String = "youtube#channel",
    @Json(name = "channelId") val channelId: String
)
