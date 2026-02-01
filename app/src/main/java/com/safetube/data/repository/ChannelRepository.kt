package com.safetube.data.repository

import com.safetube.data.remote.api.YouTubeApiService
import com.safetube.data.remote.dto.ChannelDto
import com.safetube.data.remote.dto.SearchResultDto
import com.safetube.data.remote.dto.SubscribeRequest
import com.safetube.data.remote.dto.SubscribeResourceId
import com.safetube.data.remote.dto.SubscribeSnippet
import com.safetube.data.remote.dto.SubscriptionDto
import com.safetube.data.remote.dto.YouTubeListResponse
import com.safetube.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    private val apiService: YouTubeApiService
) {

    suspend fun getChannelDetails(channelIds: List<String>): Result<List<ChannelDto>> {
        return try {
            val idsString = channelIds.joinToString(",")
            val response = apiService.getChannels(ids = idsString)
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

    suspend fun getChannelVideos(
        channelId: String,
        pageToken: String? = null,
        maxResults: Int = 20
    ): Result<YouTubeListResponse<SearchResultDto>> {
        return try {
            val response = apiService.getChannelVideos(
                channelId = channelId,
                pageToken = pageToken,
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

    suspend fun getSubscriptions(
        authToken: String,
        pageToken: String? = null,
        maxResults: Int = 50
    ): Result<YouTubeListResponse<SubscriptionDto>> {
        return try {
            val response = apiService.getSubscriptions(
                authToken = "Bearer $authToken",
                pageToken = pageToken,
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

    suspend fun subscribe(
        authToken: String,
        channelId: String
    ): Result<SubscriptionDto> {
        return try {
            val request = SubscribeRequest(
                snippet = SubscribeSnippet(
                    resourceId = SubscribeResourceId(channelId = channelId)
                )
            )
            val response = apiService.subscribe(
                authToken = "Bearer $authToken",
                request = request
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

    suspend fun unsubscribe(
        authToken: String,
        subscriptionId: String
    ): Result<Unit> {
        return try {
            val response = apiService.unsubscribe(
                authToken = "Bearer $authToken",
                subscriptionId = subscriptionId
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
