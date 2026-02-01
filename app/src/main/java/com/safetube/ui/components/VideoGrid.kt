package com.safetube.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.safetube.R
import com.safetube.domain.model.Video

@Composable
fun VideoGrid(
    videos: List<Video>,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    emptyMessage: String = stringResource(R.string.no_results),
    gridState: LazyGridState = rememberLazyGridState()
) {
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
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 300.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = videos,
                    key = { it.id }
                ) { video ->
                    VideoCard(
                        video = video,
                        onClick = { onVideoClick(video.id) }
                    )
                }

                // Loading indicator at the bottom
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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
                        val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
                            ?: return@derivedStateOf false

                        lastVisibleItem.index >= videos.size - 4
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
