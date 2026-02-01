package com.safetube.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a blocked keyword for video titles.
 * Videos with titles containing this keyword will be filtered out from results.
 */
@Entity(tableName = "blocked_keywords")
data class BlockedKeywordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val matchType: String = MatchType.CONTAINS.name,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Defines how a keyword should be matched against video titles.
 */
enum class MatchType {
    /**
     * Matches only if the keyword appears as a complete word.
     * Example: "news" matches "News" but not "newscaster"
     */
    EXACT_WORD,

    /**
     * Matches if the keyword appears anywhere in the title.
     * Example: "news" matches "newscaster", "fake news", etc.
     */
    CONTAINS,

    /**
     * Matches if any word in the title starts with the keyword.
     * Example: "news" matches "newscaster" but not "fake news"
     */
    STARTS_WITH
}
