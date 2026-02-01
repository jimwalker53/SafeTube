package com.safetube.domain.model

/**
 * Domain model representing a YouTube channel.
 */
data class Channel(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val subscriberCount: Long = 0,
    val videoCount: Long = 0,
    val customUrl: String? = null
) {
    /**
     * Returns a formatted subscriber count string (e.g., "1.2M subscribers").
     */
    fun formattedSubscriberCount(): String {
        return when {
            subscriberCount >= 1_000_000_000 -> String.format("%.1fB", subscriberCount / 1_000_000_000.0)
            subscriberCount >= 1_000_000 -> String.format("%.1fM", subscriberCount / 1_000_000.0)
            subscriberCount >= 1_000 -> String.format("%.1fK", subscriberCount / 1_000.0)
            else -> subscriberCount.toString()
        }
    }
}

/**
 * Represents a user's subscription to a channel.
 */
data class Subscription(
    val subscriptionId: String,
    val channel: Channel,
    val newItemCount: Int = 0,
    val totalItemCount: Int = 0,
    val subscribedAt: String = ""
)
