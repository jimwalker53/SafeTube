package com.safetube.data.repository

import com.safetube.data.remote.api.YouTubeApiService
import com.safetube.data.remote.dto.SearchResultDto
import com.safetube.data.remote.dto.VideoDto
import com.safetube.data.remote.dto.YouTubeListResponse
import com.safetube.domain.model.Video
import com.safetube.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val apiService: YouTubeApiService
) {

    suspend fun getPopularVideos(
        pageToken: String? = null,
        regionCode: String = "US",
        maxResults: Int = 20
    ): Result<YouTubeListResponse<VideoDto>> {
        return try {
            val response = apiService.getPopularVideos(
                pageToken = pageToken,
                regionCode = regionCode,
                maxResults = maxResults
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response")
            } else {
                Result.Error("API error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun searchVideos(
        query: String,
        pageToken: String? = null,
        maxResults: Int = 20,
        order: String = "relevance"
    ): Result<YouTubeListResponse<SearchResultDto>> {
        return try {
            val response = apiService.searchVideos(
                query = query,
                pageToken = pageToken,
                maxResults = maxResults,
                order = order
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response")
            } else {
                Result.Error("API error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getVideoDetails(videoIds: List<String>): Result<List<VideoDto>> {
        return try {
            val idsString = videoIds.joinToString(",")
            val response = apiService.getVideos(ids = idsString)
            if (response.isSuccessful) {
                response.body()?.items?.let {
                    Result.Success(it)
                } ?: Result.Success(emptyList())
            } else {
                Result.Error("API error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getRelatedVideos(
        videoId: String,
        maxResults: Int = 15
    ): Result<YouTubeListResponse<SearchResultDto>> {
        return try {
            val response = apiService.getRelatedVideos(
                videoId = videoId,
                maxResults = maxResults
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response")
            } else {
                Result.Error("API error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun rateVideo(
        authToken: String,
        videoId: String,
        rating: String
    ): Result<Unit> {
        return try {
            val response = apiService.rateVideo(
                authToken = "Bearer $authToken",
                videoId = videoId,
                rating = rating
            )
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
