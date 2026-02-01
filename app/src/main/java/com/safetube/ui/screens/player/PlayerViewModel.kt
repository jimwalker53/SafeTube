package com.safetube.ui.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.data.repository.AuthRepository
import com.safetube.data.repository.FilterRepository
import com.safetube.data.repository.VideoRepository
import com.safetube.domain.filter.ContentFilterEngine
import com.safetube.domain.model.Video
import com.safetube.util.Constants
import com.safetube.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository,
    private val authRepository: AuthRepository,
    private val filterRepository: FilterRepository,
    private val contentFilterEngine: ContentFilterEngine
) : ViewModel() {

    private val videoId: String = savedStateHandle["videoId"] ?: ""

    private val _uiState = MutableStateFlow(PlayerUiState(videoId = videoId))
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        loadVideoDetails()
        loadRelatedVideos()
    }

    private fun loadVideoDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = videoRepository.getVideoDetails(listOf(videoId))) {
                is Result.Success -> {
                    val videoDto = result.data.firstOrNull()
                    if (videoDto != null) {
                        val video = Video(
                            id = videoDto.id ?: videoId,
                            title = videoDto.snippet?.title ?: "",
                            description = videoDto.snippet?.description ?: "",
                            thumbnailUrl = videoDto.snippet?.thumbnails?.high?.url ?: "",
                            channelId = videoDto.snippet?.channelId ?: "",
                            channelTitle = videoDto.snippet?.channelTitle ?: "",
                            publishedAt = videoDto.snippet?.publishedAt ?: "",
                            viewCount = videoDto.statistics?.viewCount?.toLongOrNull() ?: 0,
                            likeCount = videoDto.statistics?.likeCount?.toLongOrNull() ?: 0,
                            duration = videoDto.contentDetails?.duration ?: ""
                        )
                        _uiState.update { state ->
                            state.copy(
                                video = video,
                                isLoading = false,
                                error = null
                            )
                        }

                        // Check if channel is blocked
                        checkChannelBlocked(video.channelId)
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = "Video not found"
                            )
                        }
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Result.Loading -> {}
            }
        }
    }

    private fun loadRelatedVideos() {
        viewModelScope.launch {
            when (val result = videoRepository.getRelatedVideos(videoId)) {
                is Result.Success -> {
                    val videos = result.data.items?.mapNotNull { dto ->
                        val id = dto.id?.videoId ?: return@mapNotNull null
                        Video(
                            id = id,
                            title = dto.snippet?.title ?: "",
                            description = dto.snippet?.description ?: "",
                            thumbnailUrl = dto.snippet?.thumbnails?.high?.url
                                ?: dto.snippet?.thumbnails?.medium?.url
                                ?: dto.snippet?.thumbnails?.default?.url
                                ?: "",
                            channelId = dto.snippet?.channelId ?: "",
                            channelTitle = dto.snippet?.channelTitle ?: "",
                            publishedAt = dto.snippet?.publishedAt ?: "",
                            isLive = dto.snippet?.liveBroadcastContent == "live"
                        )
                    } ?: emptyList()

                    // Filter related videos
                    val filteredVideos = contentFilterEngine.filterVideos(videos)

                    _uiState.update { state ->
                        state.copy(relatedVideos = filteredVideos)
                    }
                }

                is Result.Error -> {
                    // Silent fail for related videos
                }

                is Result.Loading -> {}
            }
        }
    }

    private suspend fun checkChannelBlocked(channelId: String) {
        val isBlocked = filterRepository.isChannelBlocked(channelId)
        _uiState.update { state ->
            state.copy(isChannelBlocked = isBlocked)
        }
    }

    fun likeVideo() {
        viewModelScope.launch {
            val authToken = authRepository.getAccessToken() ?: return@launch

            when (videoRepository.rateVideo(authToken, videoId, Constants.Rating.LIKE)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(isLiked = true)
                    }
                }

                is Result.Error -> {
                    // Handle error
                }

                is Result.Loading -> {}
            }
        }
    }

    fun blockChannel() {
        viewModelScope.launch {
            val video = _uiState.value.video ?: return@launch
            filterRepository.addBlockedChannel(
                channelId = video.channelId,
                channelName = video.channelTitle,
                channelThumbnail = null
            )
            _uiState.update { state ->
                state.copy(isChannelBlocked = true)
            }
        }
    }
}

data class PlayerUiState(
    val videoId: String = "",
    val video: Video? = null,
    val relatedVideos: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLiked: Boolean = false,
    val isChannelBlocked: Boolean = false
)
