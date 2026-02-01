package com.safetube.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.safetube.R

/**
 * Bottom navigation items matching YouTube's layout.
 * Order: Home, Shorts, Subscriptions, Library
 */
enum class BottomNavItem(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    SHORTS(R.string.nav_shorts, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary), // Using VideoLibrary as Shorts placeholder
    SUBSCRIPTIONS(R.string.nav_subscriptions, Icons.Filled.Subscriptions, Icons.Outlined.Subscriptions),
    LIBRARY(R.string.nav_library, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary)
}

/**
 * YouTube-style bottom navigation bar.
 */
@Composable
fun BottomNavBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = item == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                icon = {
                    if (item == BottomNavItem.SHORTS) {
                        // Custom Shorts icon (vertical rectangle like YouTube Shorts)
                        ShortsIcon(
                            isSelected = isSelected,
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = stringResource(item.labelRes),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * YouTube Shorts icon - a vertical play button shape.
 */
@Composable
private fun ShortsIcon(
    isSelected: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    // Use a simple play icon for now - in production, you'd use a custom drawable
    // that matches YouTube's Shorts icon (vertical rectangle with play triangle)
    Icon(
        imageVector = if (isSelected) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
        contentDescription = stringResource(R.string.nav_shorts),
        tint = tint,
        modifier = modifier.size(24.dp)
    )
}
