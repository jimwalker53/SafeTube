package com.safetube.domain.filter

import com.safetube.data.local.database.entities.MatchType
import com.safetube.data.repository.FilterRepository
import com.safetube.domain.model.BlockedChannel
import com.safetube.domain.model.BlockedKeyword
import com.safetube.domain.model.Video
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Content filtering engine that filters videos based on blocked terms,
 * keywords, and channels.
 */
@Singleton
class ContentFilterEngine @Inject constructor(
    private val filterRepository: FilterRepository
) {

    /**
     * Filters a list of videos, removing any that match blocked keywords or channels.
     *
     * @param videos The list of videos to filter
     * @return Filtered list with blocked content removed
     */
    suspend fun filterVideos(videos: List<Video>): List<Video> {
        val blockedKeywords = filterRepository.getActiveBlockedKeywords()
        val blockedChannels = filterRepository.getActiveBlockedChannels()

        return videos.filter { video ->
            !isChannelBlocked(video.channelId, blockedChannels) &&
                    !isTitleBlocked(video.title, blockedKeywords) &&
                    !isDescriptionBlocked(video.description, blockedKeywords)
        }
    }

    /**
     * Checks if a search term is blocked.
     *
     * @param query The search query to check
     * @return True if the search term is blocked
     */
    suspend fun isSearchTermBlocked(query: String): Boolean {
        val blockedTerms = filterRepository.getActiveBlockedTerms()
        val normalizedQuery = query.lowercase().trim()

        return blockedTerms.any { term ->
            normalizedQuery.contains(term.term.lowercase())
        }
    }

    /**
     * Checks if a channel is blocked.
     */
    private fun isChannelBlocked(
        channelId: String,
        blockedChannels: List<BlockedChannel>
    ): Boolean {
        return blockedChannels.any { it.channelId == channelId }
    }

    /**
     * Checks if a video title contains any blocked keywords.
     */
    private fun isTitleBlocked(
        title: String,
        blockedKeywords: List<BlockedKeyword>
    ): Boolean {
        return blockedKeywords.any { keyword ->
            matchesKeyword(title, keyword)
        }
    }

    /**
     * Checks if a video description contains any blocked keywords.
     * This is a secondary filter and may be more lenient.
     */
    private fun isDescriptionBlocked(
        description: String,
        blockedKeywords: List<BlockedKeyword>
    ): Boolean {
        // Only check for exact word matches in descriptions to reduce false positives
        return blockedKeywords
            .filter { it.matchType == MatchType.EXACT_WORD }
            .any { keyword ->
                matchesKeyword(description, keyword)
            }
    }

    /**
     * Checks if text matches a blocked keyword based on the match type.
     */
    private fun matchesKeyword(text: String, keyword: BlockedKeyword): Boolean {
        val normalizedText = text.lowercase()
        val normalizedKeyword = keyword.keyword.lowercase()

        return when (keyword.matchType) {
            MatchType.EXACT_WORD -> {
                // Match only complete words
                val pattern = "\\b${Regex.escape(normalizedKeyword)}\\b"
                pattern.toRegex().containsMatchIn(normalizedText)
            }

            MatchType.CONTAINS -> {
                // Match if the keyword appears anywhere
                normalizedText.contains(normalizedKeyword)
            }

            MatchType.STARTS_WITH -> {
                // Match if any word starts with the keyword
                normalizedText.split("\\s+".toRegex()).any { word ->
                    word.startsWith(normalizedKeyword)
                }
            }
        }
    }

    /**
     * Gets filter statistics for debugging/admin purposes.
     */
    suspend fun getFilterStats(): FilterStats {
        val terms = filterRepository.getActiveBlockedTerms()
        val keywords = filterRepository.getActiveBlockedKeywords()
        val channels = filterRepository.getActiveBlockedChannels()

        return FilterStats(
            activeBlockedTerms = terms.size,
            activeBlockedKeywords = keywords.size,
            activeBlockedChannels = channels.size
        )
    }
}

/**
 * Statistics about active content filters.
 */
data class FilterStats(
    val activeBlockedTerms: Int,
    val activeBlockedKeywords: Int,
    val activeBlockedChannels: Int
)
