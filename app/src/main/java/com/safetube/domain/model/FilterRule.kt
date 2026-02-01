package com.safetube.domain.model

import com.safetube.data.local.database.entities.MatchType

/**
 * Represents a blocked search term.
 */
data class BlockedTerm(
    val id: Long = 0,
    val term: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a blocked keyword for video titles.
 */
data class BlockedKeyword(
    val id: Long = 0,
    val keyword: String,
    val matchType: MatchType = MatchType.CONTAINS,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a blocked YouTube channel.
 */
data class BlockedChannel(
    val id: Long = 0,
    val channelId: String,
    val channelName: String,
    val channelThumbnail: String? = null,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
