package com.safetube.domain.usecase

import com.safetube.domain.filter.ContentFilterEngine
import com.safetube.domain.model.Video
import javax.inject.Inject

/**
 * Use case for filtering a list of videos.
 * This can be used when videos are already loaded but need to be re-filtered
 * (e.g., after filter rules change).
 */
class FilterVideosUseCase @Inject constructor(
    private val contentFilterEngine: ContentFilterEngine
) {

    suspend operator fun invoke(videos: List<Video>): List<Video> {
        return contentFilterEngine.filterVideos(videos)
    }

    suspend fun isSearchBlocked(query: String): Boolean {
        return contentFilterEngine.isSearchTermBlocked(query)
    }
}
