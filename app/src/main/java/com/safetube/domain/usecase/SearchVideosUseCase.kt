package com.safetube.domain.usecase

import com.safetube.data.remote.dto.SearchResultDto
import com.safetube.data.repository.VideoRepository
import com.safetube.domain.filter.ContentFilterEngine
import com.safetube.domain.model.Video
import com.safetube.util.Result
import javax.inject.Inject

/**
 * Use case for searching videos.
 * Checks if the search term is blocked and applies content filtering to results.
 */
class SearchVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val contentFilterEngine: ContentFilterEngine
) {

    suspend operator fun invoke(
        query: String,
        pageToken: String? = null,
        maxResults: Int = 20
    ): Result<SearchVideosResult> {
        // Check if the search term itself is blocked
        if (contentFilterEngine.isSearchTermBlocked(query)) {
            return Result.Success(
                SearchVideosResult(
                    videos = emptyList(),
                    nextPageToken = null,
                    isSearchBlocked = true
                )
            )
        }

        return when (val result = videoRepository.searchVideos(query, pageToken, maxResults)) {
            is Result.Success -> {
                val videos = result.data.items?.map { it.toDomain() } ?: emptyList()
                val filteredVideos = contentFilterEngine.filterVideos(videos)

                Result.Success(
                    SearchVideosResult(
                        videos = filteredVideos,
                        nextPageToken = result.data.nextPageToken,
                        isSearchBlocked = false
                    )
                )
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    private fun SearchResultDto.toDomain(): Video {
        return Video(
            id = id?.videoId ?: "",
            title = snippet?.title ?: "",
            description = snippet?.description ?: "",
            thumbnailUrl = snippet?.thumbnails?.high?.url
                ?: snippet?.thumbnails?.medium?.url
                ?: snippet?.thumbnails?.default?.url
                ?: "",
            channelId = snippet?.channelId ?: "",
            channelTitle = snippet?.channelTitle ?: "",
            publishedAt = snippet?.publishedAt ?: "",
            isLive = snippet?.liveBroadcastContent == "live"
        )
    }
}

data class SearchVideosResult(
    val videos: List<Video>,
    val nextPageToken: String?,
    val isSearchBlocked: Boolean = false
)
