package com.safetube.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.domain.model.Video
import com.safetube.domain.usecase.SearchVideosUseCase
import com.safetube.util.Constants
import com.safetube.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(Constants.SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query ->
                    performSearch(query, refresh = true)
                }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            performSearch(query, refresh = true)
        }
    }

    private fun performSearch(query: String, refresh: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { state ->
                if (refresh) {
                    state.copy(isLoading = true, error = null, videos = emptyList())
                } else {
                    state.copy(isLoading = true)
                }
            }

            val pageToken = if (refresh) null else _uiState.value.nextPageToken

            when (val result = searchVideosUseCase(query, pageToken)) {
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
                            error = null,
                            hasSearched = true
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message,
                            hasSearched = true
                        )
                    }
                }

                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun loadMore() {
        if (_uiState.value.nextPageToken != null && !_uiState.value.isLoading) {
            performSearch(_uiState.value.query, refresh = false)
        }
    }

    fun clearSearch() {
        _uiState.update {
            SearchUiState()
        }
        _searchQuery.value = ""
    }
}

data class SearchUiState(
    val query: String = "",
    val videos: List<Video> = emptyList(),
    val nextPageToken: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)
