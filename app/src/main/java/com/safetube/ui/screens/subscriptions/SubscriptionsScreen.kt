package com.safetube.ui.screens.subscriptions

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.safetube.R
import com.safetube.domain.model.Subscription
import com.safetube.ui.components.BottomNavBar
import com.safetube.ui.components.BottomNavItem
import com.safetube.ui.components.VideoFeed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onVideoClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: (() -> Unit)? = null,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val displayedVideos = viewModel.getDisplayedVideos()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = BottomNavItem.SUBSCRIPTIONS,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> onNavigateToHome?.invoke() ?: onNavigateBack()
                        BottomNavItem.SHORTS -> { /* Shorts not implemented */ }
                        BottomNavItem.SUBSCRIPTIONS -> { /* Already here */ }
                        BottomNavItem.LIBRARY -> { /* Library not implemented */ }
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.error != null && uiState.subscriptions.isEmpty()) {
                ErrorState(
                    message = uiState.error ?: stringResource(R.string.error_generic),
                    onRetry = { viewModel.loadSubscriptions(refresh = true) }
                )
            } else if (uiState.subscriptions.isEmpty() && !uiState.isLoading) {
                EmptyState()
            } else {
                VideoFeed(
                    videos = displayedVideos,
                    onVideoClick = onVideoClick,
                    isLoading = uiState.isLoadingVideos,
                    header = {
                        SubscriptionChannelsRow(
                            subscriptions = uiState.subscriptions,
                            selectedChannelId = uiState.selectedChannelId,
                            onChannelSelected = { viewModel.selectChannel(it) }
                        )
                    }
                )
            }
        }
    }
}

/**
 * Horizontal scrollable row of channel avatars for filtering.
 * First item is "All" to show all subscriptions.
 */
@Composable
private fun SubscriptionChannelsRow(
    subscriptions: List<Subscription>,
    selectedChannelId: String?,
    onChannelSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "All" option
        ChannelFilterItem(
            thumbnailUrl = null,
            title = "All",
            isSelected = selectedChannelId == null,
            onClick = { onChannelSelected(null) }
        )

        // Channel avatars
        subscriptions.forEach { subscription ->
            ChannelFilterItem(
                thumbnailUrl = subscription.channel.thumbnailUrl,
                title = subscription.channel.title,
                isSelected = selectedChannelId == subscription.channel.id,
                hasNewContent = subscription.newItemCount > 0,
                onClick = { onChannelSelected(subscription.channel.id) }
            )
        }
    }
}

@Composable
private fun ChannelFilterItem(
    thumbnailUrl: String?,
    title: String,
    isSelected: Boolean,
    hasNewContent: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        ),
                    contentScale = ContentScale.Crop
                )

                // New content indicator (blue dot)
                if (hasNewContent && !isSelected) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp),
                        shape = CircleShape,
                        color = Color(0xFF065FD4) // YouTube blue
                    ) {}
                }
            } else {
                // "All" icon
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        ),
                    shape = CircleShape,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "All",
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_subscriptions),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
