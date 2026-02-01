package com.safetube.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.domain.model.Video
import com.safetube.domain.usecase.GetHomeVideosUseCase
import com.safetube.ui.components.VideoCategories
import com.safetube.ui.components.VideoCategory
import com.safetube.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeVideosUseCase: GetHomeVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { state ->
                if (refresh) {
                    state.copy(isLoading = true, error = null)
                } else {
                    state.copy(isLoading = true)
                }
            }

            val pageToken = if (refresh) null else _uiState.value.nextPageToken
            val categoryId = _uiState.value.selectedCategory.id.takeIf { it != "0" }

            when (val result = getHomeVideosUseCase(pageToken = pageToken, categoryId = categoryId)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        val newVideos = if (refresh) {
                            result.data.videos
                        } else {
                            state.videos + result.data.videos
                        }
                        state.copy(
                            videos = newVideos,
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

                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun selectCategory(category: VideoCategory) {
        if (category.id == _uiState.value.selectedCategory.id) return

        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                videos = emptyList(),
                nextPageToken = null
            )
        }
        loadVideos(refresh = true)
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadVideos(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.nextPageToken != null && !_uiState.value.isLoading) {
            loadVideos(refresh = false)
        }
    }
}

data class HomeUiState(
    val videos: List<Video> = emptyList(),
    val nextPageToken: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedCategory: VideoCategory = VideoCategories.ALL
)
