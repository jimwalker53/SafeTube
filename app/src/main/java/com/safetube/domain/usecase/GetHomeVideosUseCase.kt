package com.safetube.domain.usecase

import com.safetube.data.remote.dto.VideoDto
import com.safetube.data.repository.VideoRepository
import com.safetube.domain.filter.ContentFilterEngine
import com.safetube.domain.model.Video
import com.safetube.util.Result
import javax.inject.Inject

/**
 * Use case for fetching home screen videos (popular/trending).
 * Automatically applies content filtering.
 */
class GetHomeVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val contentFilterEngine: ContentFilterEngine
) {

    suspend operator fun invoke(
        pageToken: String? = null,
        maxResults: Int = 20,
        categoryId: String? = null
    ): Result<HomeVideosResult> {
        return when (val result = videoRepository.getPopularVideos(
            pageToken = pageToken,
            maxResults = maxResults,
            categoryId = categoryId
        )) {
            is Result.Success -> {
                val videos = result.data.items?.map { it.toDomain() } ?: emptyList()
                val filteredVideos = contentFilterEngine.filterVideos(videos)

                Result.Success(
                    HomeVideosResult(
                        videos = filteredVideos,
                        nextPageToken = result.data.nextPageToken
                    )
                )
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    private fun VideoDto.toDomain(): Video {
        return Video(
            id = id ?: "",
            title = snippet?.title ?: "",
            description = snippet?.description ?: "",
            thumbnailUrl = snippet?.thumbnails?.high?.url
                ?: snippet?.thumbnails?.medium?.url
                ?: snippet?.thumbnails?.default?.url
                ?: "",
            channelId = snippet?.channelId ?: "",
            channelTitle = snippet?.channelTitle ?: "",
            publishedAt = snippet?.publishedAt ?: "",
            viewCount = statistics?.viewCount?.toLongOrNull() ?: 0,
            likeCount = statistics?.likeCount?.toLongOrNull() ?: 0,
            duration = contentDetails?.duration ?: "",
            isLive = snippet?.liveBroadcastContent == "live"
        )
    }
}

data class HomeVideosResult(
    val videos: List<Video>,
    val nextPageToken: String?
)
