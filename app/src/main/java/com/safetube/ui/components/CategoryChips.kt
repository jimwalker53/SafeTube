package com.safetube.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Video category for filtering content.
 */
data class VideoCategory(
    val id: String,
    val name: String
)

/**
 * Default categories matching YouTube's category chips.
 */
object VideoCategories {
    val ALL = VideoCategory("0", "All")
    val MUSIC = VideoCategory("10", "Music")
    val GAMING = VideoCategory("20", "Gaming")
    val NEWS = VideoCategory("25", "News")
    val SPORTS = VideoCategory("17", "Sports")
    val ENTERTAINMENT = VideoCategory("24", "Entertainment")
    val COMEDY = VideoCategory("23", "Comedy")
    val FILM = VideoCategory("1", "Film & Animation")
    val HOWTO = VideoCategory("26", "Howto & Style")
    val SCIENCE = VideoCategory("28", "Science & Tech")

    val defaultCategories = listOf(
        ALL, MUSIC, GAMING, NEWS, SPORTS,
        ENTERTAINMENT, COMEDY, FILM, HOWTO, SCIENCE
    )
}

/**
 * Horizontal scrolling category filter chips like YouTube's home screen.
 */
@Composable
fun CategoryChips(
    categories: List<VideoCategory> = VideoCategories.defaultCategories,
    selectedCategory: VideoCategory = VideoCategories.ALL,
    onCategorySelected: (VideoCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category.id == selectedCategory.id

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.onSurface,
                    selectedLabelColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
