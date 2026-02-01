package com.safetube.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultDto(
    @Json(name = "kind") val kind: String?,
    @Json(name = "etag") val etag: String?,
    @Json(name = "id") val id: SearchResultId?,
    @Json(name = "snippet") val snippet: SearchSnippet?
)

@JsonClass(generateAdapter = true)
data class SearchResultId(
    @Json(name = "kind") val kind: String?,
    @Json(name = "videoId") val videoId: String?,
    @Json(name = "channelId") val channelId: String?,
    @Json(name = "playlistId") val playlistId: String?
)

@JsonClass(generateAdapter = true)
data class SearchSnippet(
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "channelId") val channelId: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "thumbnails") val thumbnails: Thumbnails?,
    @Json(name = "channelTitle") val channelTitle: String?,
    @Json(name = "liveBroadcastContent") val liveBroadcastContent: String?
)
