package com.safetube.domain.model

/**
 * Domain model representing a YouTube video.
 */
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelId: String,
    val channelTitle: String,
    val channelThumbnailUrl: String = "",
    val publishedAt: String,
    val viewCount: Long = 0,
    val likeCount: Long = 0,
    val duration: String = "",
    val isLive: Boolean = false
) {
    /**
     * Returns the best available thumbnail URL.
     * Prefers high quality, falls back to medium or default.
     */
    fun getBestThumbnail(preferredSize: ThumbnailSize = ThumbnailSize.HIGH): String {
        return thumbnailUrl
    }

    /**
     * Returns a formatted view count string (e.g., "1.2M views").
     */
    fun formattedViewCount(): String {
        return when {
            viewCount >= 1_000_000_000 -> String.format("%.1fB", viewCount / 1_000_000_000.0)
            viewCount >= 1_000_000 -> String.format("%.1fM", viewCount / 1_000_000.0)
            viewCount >= 1_000 -> String.format("%.1fK", viewCount / 1_000.0)
            else -> viewCount.toString()
        }
    }

    /**
     * Returns a formatted duration string (e.g., "10:30").
     */
    fun formattedDuration(): String {
        if (duration.isEmpty()) return ""

        // Parse ISO 8601 duration (PT#H#M#S)
        val regex = "PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?".toRegex()
        val match = regex.matchEntire(duration) ?: return duration

        val hours = match.groupValues[1].toIntOrNull() ?: 0
        val minutes = match.groupValues[2].toIntOrNull() ?: 0
        val seconds = match.groupValues[3].toIntOrNull() ?: 0

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%d:%02d", minutes, seconds)
        }
    }
}

enum class ThumbnailSize {
    DEFAULT,
    MEDIUM,
    HIGH,
    STANDARD,
    MAXRES
}
