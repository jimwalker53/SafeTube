package com.safetube.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.safetube.R
import com.safetube.domain.model.Video

/**
 * YouTube-style video feed - vertical scrolling list of video cards.
 * Adapts to screen size: single column on phones, two columns on tablets.
 */
@Composable
fun VideoFeed(
    videos: List<Video>,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    emptyMessage: String = stringResource(R.string.no_results),
    listState: LazyListState = rememberLazyListState(),
    header: @Composable (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    // Use two columns only on large tablets (>= 840dp width)
    val useDoubleColumn = screenWidthDp >= 840

    Box(modifier = modifier.fillMaxSize()) {
        if (videos.isEmpty() && !isLoading) {
            // Empty state
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Optional header (for category chips, etc.)
                if (header != null) {
                    item {
                        header()
                    }
                }

                if (useDoubleColumn) {
                    // Two-column layout for tablets
                    val chunkedVideos = videos.chunked(2)
                    items(
                        items = chunkedVideos,
                        key = { pair -> pair.first().id }
                    ) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { video ->
                                YouTubeVideoCard(
                                    video = video,
                                    onClick = { onVideoClick(video.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty space if odd number of videos
                            if (pair.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    // Single column layout for phones
                    items(
                        items = videos,
                        key = { it.id }
                    ) { video ->
                        YouTubeVideoCard(
                            video = video,
                            onClick = { onVideoClick(video.id) }
                        )
                    }
                }

                // Loading indicator at the bottom
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Detect when we need to load more
            if (onLoadMore != null) {
                val shouldLoadMore by remember {
                    derivedStateOf {
                        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                            ?: return@derivedStateOf false

                        lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3
                    }
                }

                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore && !isLoading) {
                        onLoadMore()
                    }
                }
            }
        }

        // Full screen loading
        if (isLoading && videos.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Legacy VideoGrid - now delegates to VideoFeed for YouTube-style layout.
 */
@Composable
fun VideoGrid(
    videos: List<Video>,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    emptyMessage: String = stringResource(R.string.no_results)
) {
    VideoFeed(
        videos = videos,
        onVideoClick = onVideoClick,
        modifier = modifier,
        isLoading = isLoading,
        onLoadMore = onLoadMore,
        emptyMessage = emptyMessage
    )
}
