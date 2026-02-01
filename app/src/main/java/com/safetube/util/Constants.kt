package com.safetube.util

object Constants {

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_PAGE_SIZE = 50

    // PIN
    const val PIN_MIN_LENGTH = 4
    const val PIN_MAX_LENGTH = 6
    const val PIN_MAX_ATTEMPTS = 10
    const val PIN_LOCKOUT_DURATION_SHORT = 30_000L // 30 seconds
    const val PIN_LOCKOUT_DURATION_LONG = 300_000L // 5 minutes

    // UI
    const val LONG_PRESS_DURATION = 3000L // 3 seconds for settings access
    const val SEARCH_DEBOUNCE_MS = 500L

    // Cache
    const val VIDEO_CACHE_DURATION_MS = 300_000L // 5 minutes

    // Video Quality Options
    object VideoQuality {
        const val AUTO = "auto"
        const val HIGH = "hd1080"
        const val MEDIUM = "hd720"
        const val LOW = "large" // 480p
    }

    // Rating values for YouTube API
    object Rating {
        const val LIKE = "like"
        const val DISLIKE = "dislike"
        const val NONE = "none"
    }
}
