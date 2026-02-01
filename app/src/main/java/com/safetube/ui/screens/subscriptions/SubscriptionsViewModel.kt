package com.safetube.ui.screens.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.data.repository.AuthRepository
import com.safetube.data.repository.ChannelRepository
import com.safetube.domain.filter.ContentFilterEngine
import com.safetube.domain.model.Channel
import com.safetube.domain.model.Subscription
import com.safetube.domain.model.Video
import com.safetube.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val authRepository: AuthRepository,
    private val contentFilterEngine: ContentFilterEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionsUiState())
    val uiState: StateFlow<SubscriptionsUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptions()
    }

    fun loadSubscriptions(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, error = null)
            }

            val authToken = authRepository.getAccessToken()
            if (authToken == null) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Please sign in to view subscriptions"
                    )
                }
                return@launch
            }

            val pageToken = if (refresh) null else _uiState.value.nextPageToken

            when (val result = channelRepository.getSubscriptions(authToken, pageToken)) {
                is Result.Success -> {
                    val subscriptions = result.data.items?.map { dto ->
                        Subscription(
                            subscriptionId = dto.id ?: "",
                            channel = Channel(
                                id = dto.snippet?.resourceId?.channelId ?: "",
                                title = dto.snippet?.title ?: "",
                                description = dto.snippet?.description ?: "",
                                thumbnailUrl = dto.snippet?.thumbnails?.high?.url
                                    ?: dto.snippet?.thumbnails?.medium?.url
                                    ?: dto.snippet?.thumbnails?.default?.url
                                    ?: ""
                            ),
                            newItemCount = dto.contentDetails?.newItemCount ?: 0,
                            totalItemCount = dto.contentDetails?.totalItemCount ?: 0
                        )
                    } ?: emptyList()

                    _uiState.update { state ->
                        val newSubscriptions = if (refresh) {
                            subscriptions
                        } else {
                            state.subscriptions + subscriptions
                        }
                        state.copy(
                            subscriptions = newSubscriptions,
                            nextPageToken = result.data.nextPageToken,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }

                is Result.Loading -> {}
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadSubscriptions(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.nextPageToken != null && !_uiState.value.isLoading) {
            loadSubscriptions(refresh = false)
        }
    }

    fun selectChannel(subscription: Subscription) {
        _uiState.update { state ->
            state.copy(selectedChannel = subscription.channel)
        }
        loadChannelVideos(subscription.channel.id)
    }

    fun clearSelectedChannel() {
        _uiState.update { state ->
            state.copy(
                selectedChannel = null,
                channelVideos = emptyList()
            )
        }
    }

    private fun loadChannelVideos(channelId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingChannelVideos = true) }

            when (val result = channelRepository.getChannelVideos(channelId)) {
                is Result.Success -> {
                    val videos = result.data.items?.mapNotNull { dto ->
                        val videoId = dto.id?.videoId ?: return@mapNotNull null
                        Video(
                            id = videoId,
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

                    // Filter videos
                    val filteredVideos = contentFilterEngine.filterVideos(videos)

                    _uiState.update { state ->
                        state.copy(
                            channelVideos = filteredVideos,
                            isLoadingChannelVideos = false
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoadingChannelVideos = false,
                            error = result.message
                        )
                    }
                }

                is Result.Loading -> {}
            }
        }
    }
}

data class SubscriptionsUiState(
    val subscriptions: List<Subscription> = emptyList(),
    val nextPageToken: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedChannel: Channel? = null,
    val channelVideos: List<Video> = emptyList(),
    val isLoadingChannelVideos: Boolean = false
)
