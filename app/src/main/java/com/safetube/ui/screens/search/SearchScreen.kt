package com.safetube.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.safetube.R
import com.safetube.ui.components.SearchBar
import com.safetube.ui.components.VideoGrid

@Composable
fun SearchScreen(
    onVideoClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.query,
                onQueryChange = { viewModel.updateQuery(it) },
                onSearch = { viewModel.search() },
                onNavigateBack = onNavigateBack,
                autoFocus = true
            )

            VideoGrid(
                videos = uiState.videos,
                onVideoClick = onVideoClick,
                isLoading = uiState.isLoading,
                onLoadMore = { viewModel.loadMore() },
                emptyMessage = if (uiState.hasSearched) {
                    stringResource(R.string.no_results)
                } else {
                    stringResource(R.string.search_hint)
                }
            )
        }
    }
}
