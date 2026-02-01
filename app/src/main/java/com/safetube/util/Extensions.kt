package com.safetube.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Converts an ISO 8601 date string to a relative time string (e.g., "2 days ago").
 */
fun String.toRelativeTime(): String {
    try {
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        )

        var date: Date? = null
        for (format in formats) {
            try {
                date = format.parse(this)
                if (date != null) break
            } catch (e: Exception) {
                continue
            }
        }

        if (date == null) return this

        val now = System.currentTimeMillis()
        val diff = now - date.time

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
            days < 30 -> "${days / 7} week${if (days / 7 > 1) "s" else ""} ago"
            days < 365 -> "${days / 30} month${if (days / 30 > 1) "s" else ""} ago"
            else -> "${days / 365} year${if (days / 365 > 1) "s" else ""} ago"
        }
    } catch (e: Exception) {
        return this
    }
}

/**
 * Formats a large number to a human-readable string (e.g., 1.2M, 500K).
 */
fun Long.formatCount(): String {
    return when {
        this >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", this / 1_000_000_000.0)
        this >= 1_000_000 -> String.format(Locale.US, "%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format(Locale.US, "%.1fK", this / 1_000.0)
        else -> this.toString()
    }
}

/**
 * Parses an ISO 8601 duration string to human-readable format.
 */
fun String.parseDuration(): String {
    if (this.isEmpty()) return ""

    val regex = "PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?".toRegex()
    val match = regex.matchEntire(this) ?: return this

    val hours = match.groupValues[1].toIntOrNull() ?: 0
    val minutes = match.groupValues[2].toIntOrNull() ?: 0
    val seconds = match.groupValues[3].toIntOrNull() ?: 0

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%d:%02d", minutes, seconds)
    }
}
