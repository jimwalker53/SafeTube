package com.safetube.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Generic YouTube API list response wrapper.
 */
@JsonClass(generateAdapter = true)
data class YouTubeListResponse<T>(
    @Json(name = "kind") val kind: String?,
    @Json(name = "etag") val etag: String?,
    @Json(name = "nextPageToken") val nextPageToken: String?,
    @Json(name = "prevPageToken") val prevPageToken: String?,
    @Json(name = "pageInfo") val pageInfo: PageInfo?,
    @Json(name = "items") val items: List<T>?
)

@JsonClass(generateAdapter = true)
data class PageInfo(
    @Json(name = "totalResults") val totalResults: Int?,
    @Json(name = "resultsPerPage") val resultsPerPage: Int?
)
