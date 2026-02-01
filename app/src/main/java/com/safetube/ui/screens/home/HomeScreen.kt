package com.safetube.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.safetube.R
import com.safetube.ui.components.BottomNavBar
import com.safetube.ui.components.BottomNavItem
import com.safetube.ui.components.VideoGrid
import com.safetube.util.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onVideoClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onLongPressLogo: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var pressStartTime by remember { mutableStateOf(0L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.combinedClickable(
                            onClick = { /* Normal click does nothing */ },
                            onLongClick = {
                                val pressDuration = System.currentTimeMillis() - pressStartTime
                                if (pressDuration >= Constants.LONG_PRESS_DURATION) {
                                    onLongPressLogo()
                                }
                            },
                            onClickLabel = null,
                            onLongClickLabel = null
                        )
                    ) {
                        androidx.compose.foundation.layout.Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.combinedClickable(
                                onClick = { },
                                onLongClick = { onLongPressLogo() }
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = BottomNavItem.HOME,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> { /* Already here */ }
                        BottomNavItem.SEARCH -> onNavigateToSearch()
                        BottomNavItem.SUBSCRIPTIONS -> onNavigateToSubscriptions()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.error != null && uiState.videos.isEmpty()) {
                    ErrorState(
                        message = uiState.error ?: stringResource(R.string.error_generic),
                        onRetry = { viewModel.loadVideos(refresh = true) }
                    )
                } else {
                    VideoGrid(
                        videos = uiState.videos,
                        onVideoClick = onVideoClick,
                        isLoading = uiState.isLoading,
                        onLoadMore = { viewModel.loadMore() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.material3.TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}
