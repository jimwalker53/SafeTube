package com.safetube.domain.usecase

import com.safetube.data.local.database.entities.MatchType
import com.safetube.data.repository.FilterRepository
import com.safetube.domain.model.BlockedChannel
import com.safetube.domain.model.BlockedKeyword
import com.safetube.domain.model.BlockedTerm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing content filter rules.
 */
class ManageFiltersUseCase @Inject constructor(
    private val filterRepository: FilterRepository
) {

    // Blocked Terms
    fun getAllBlockedTerms(): Flow<List<BlockedTerm>> {
        return filterRepository.getAllBlockedTerms()
    }

    suspend fun addBlockedTerm(term: String): Long {
        return filterRepository.addBlockedTerm(term.trim())
    }

    suspend fun updateBlockedTerm(blockedTerm: BlockedTerm) {
        filterRepository.updateBlockedTerm(blockedTerm)
    }

    suspend fun deleteBlockedTerm(id: Long) {
        filterRepository.deleteBlockedTerm(id)
    }

    suspend fun setBlockedTermEnabled(id: Long, isEnabled: Boolean) {
        filterRepository.setBlockedTermEnabled(id, isEnabled)
    }

    // Blocked Keywords
    fun getAllBlockedKeywords(): Flow<List<BlockedKeyword>> {
        return filterRepository.getAllBlockedKeywords()
    }

    suspend fun addBlockedKeyword(keyword: String, matchType: MatchType): Long {
        return filterRepository.addBlockedKeyword(keyword.trim(), matchType)
    }

    suspend fun updateBlockedKeyword(blockedKeyword: BlockedKeyword) {
        filterRepository.updateBlockedKeyword(blockedKeyword)
    }

    suspend fun deleteBlockedKeyword(id: Long) {
        filterRepository.deleteBlockedKeyword(id)
    }

    suspend fun setBlockedKeywordEnabled(id: Long, isEnabled: Boolean) {
        filterRepository.setBlockedKeywordEnabled(id, isEnabled)
    }

    // Blocked Channels
    fun getAllBlockedChannels(): Flow<List<BlockedChannel>> {
        return filterRepository.getAllBlockedChannels()
    }

    suspend fun addBlockedChannel(
        channelId: String,
        channelName: String,
        channelThumbnail: String? = null
    ): Long {
        return filterRepository.addBlockedChannel(channelId, channelName, channelThumbnail)
    }

    suspend fun deleteBlockedChannel(id: Long) {
        filterRepository.deleteBlockedChannel(id)
    }

    suspend fun deleteBlockedChannelByChannelId(channelId: String) {
        filterRepository.deleteBlockedChannelByChannelId(channelId)
    }

    suspend fun setBlockedChannelEnabled(id: Long, isEnabled: Boolean) {
        filterRepository.setBlockedChannelEnabled(id, isEnabled)
    }

    suspend fun isChannelBlocked(channelId: String): Boolean {
        return filterRepository.isChannelBlocked(channelId)
    }
}
